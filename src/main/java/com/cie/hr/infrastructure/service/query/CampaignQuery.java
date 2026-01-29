package com.cie.hr.infrastructure.service.query;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cie.hr.common.event.StartCampaignEvent;
import com.cie.hr.common.event.listeners.SendStartCampaignEmailEventListener;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.mapper.CampaignMapper;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.CampaignJpaRepository;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import com.cie.hr.infrastructure.repository.StatusJpaRepository;
import com.cie.hr.infrastructure.service.AsyncEmailBatchService;
import com.cie.hr.infrastructure.service.viewmodel.CampaignVm;

import jakarta.mail.MessagingException;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class CampaignQuery {

    private final CampaignJpaRepository campaignJpaRepository;
    private final EmployeeJpaRepository employeeJpaRepository;
    private final AsyncEmailBatchService asyncEmailBatchService;
    private final StatusJpaRepository statusJpaRepository;
    private final ScorecardJpaRepository scorecardJpaRepository;
    private final SendStartCampaignEmailEventListener sendStartCampaignEmailEventListener;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public CampaignQuery(CampaignJpaRepository campaignJpaRepository,
                         EmployeeJpaRepository employeeJpaRepository,
                         AsyncEmailBatchService asyncEmailBatchService,
                         StatusJpaRepository statusJpaRepository,
                         ScorecardJpaRepository scorecardJpaRepository,
                         SendStartCampaignEmailEventListener sendStartCampaignEmailEventListener) {
        this.campaignJpaRepository = campaignJpaRepository;
        this.employeeJpaRepository = employeeJpaRepository;
        this.asyncEmailBatchService = asyncEmailBatchService;
        this.statusJpaRepository = statusJpaRepository;
        this.scorecardJpaRepository = scorecardJpaRepository;
        this.sendStartCampaignEmailEventListener = sendStartCampaignEmailEventListener;
    }

    public List<CampaignVm> readAll() {
        List<CampaignVm> campaignVmList = campaignJpaRepository.findByDeletedFalseOrderByCreatedDesc().stream().map(CampaignMapper::toCampaignVm).toList();

        boolean allClosed = campaignVmList.stream()
                .allMatch(campaign -> "closed".equals(campaign.status().name()));

        if (allClosed) {
            // Campagne avec la end_date la plus récente
            CampaignVm mostRecentCampaign = campaignVmList.stream()
                    .max(Comparator.comparing(CampaignVm::end_date))
                    .orElse(null);

            return campaignVmList.stream()
                    .map(campaign -> new CampaignVm(
                            campaign.id(),
                            campaign.name(),
                            campaign.start_date(),
                            campaign.end_date(),
                            campaign.status(),
                            campaign.equals(mostRecentCampaign) // canBeModified = true pour la campagne récente
                    ))
                    .collect(Collectors.toList());
        }

        boolean hasInProgressCampaign = campaignVmList.stream()
                .anyMatch(campaign -> "inProgress".equals(campaign.status().name()));


        return campaignVmList.stream()
                .map(campaign -> new CampaignVm(
                        campaign.id(),
                        campaign.name(),
                        campaign.start_date(),
                        campaign.end_date(),
                        campaign.status(),
                        "notStarted".equals(campaign.status().name()) ||
                                "inProgress".equals(campaign.status().name()) && hasInProgressCampaign
                ))
                .collect(Collectors.toList());
    }

    public Optional<CampaignVm> readCampaignDetail(UUID id) {
        return campaignJpaRepository.findById(id).map(CampaignMapper::toCampaignVm);
    }

    public Optional<CampaignVm> readCampaignByStatusCode(String code) {
        return campaignJpaRepository.findFirstByStatusCodeAndDeletedFalse(code).map(CampaignMapper::toCampaignVm);
    }

    public List<CampaignVm> readCampaignsByStatusClosed() {
        return campaignJpaRepository.findAllByStatusCodeOrderByStartDateDesc("2").stream().map(CampaignMapper::toCampaignVm).toList();
    }

    public void startCampaign(UUID campaignId) {
        LOGGER.info("Start Campaign {}", campaignId);
        Optional<CampaignEntity> campaign = campaignJpaRepository.findById(campaignId);
        campaign.ifPresent(campaignEntity -> {
            LOGGER.info("Find Campaign to open");
            
            // Envoi asynchrone par lots des emails d'identifiants
            List<EmployeeEntity> employees = employeeJpaRepository.findBySendAccountIdEmail(false);
            LOGGER.info("Lancement envoi asynchrone d'emails d'identifiants pour {} employés", employees.size());
            
            if (!employees.isEmpty()) {
                // Envoi asynchrone avec retry et batch processing
                asyncEmailBatchService.sendCredentialsEmailsAsync(employees)
                    .thenAccept(result -> {
                        LOGGER.info("Envoi emails terminé: {} succès, {} échecs (taux: {}%)",
                            result.successCount(), result.failureCount(), 
                            String.format("%.1f", result.successRate()));
                        if (result.hasFailures()) {
                            LOGGER.warn("Employés en échec: {}", result.failedEmployees());
                        }
                    });
            }

            if (campaignEntity.getStatus().getCode().equals("0")) {
                LOGGER.info("La campagne est en attente de démarrage");
                Optional<StatusEntity> statusInProgressEntity = statusJpaRepository.findByCode("1");
                if (statusInProgressEntity.isPresent()) {
                    LOGGER.info("Status inProgress found");
                    campaignEntity.setStatus(statusInProgressEntity.get());
                    campaignJpaRepository.save(campaignEntity);
                    List<StartCampaignEvent> startCampaignEvents = new ArrayList<>();
                    List<ScorecardEntity> scorecardEntityList = scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaignEntity.getId());
                    LOGGER.info("Send email startCampaignEvents size {}", scorecardEntityList.size());
                    scorecardEntityList.forEach(scorecard -> startCampaignEvents.add(new StartCampaignEvent(EmployeeMapper.toEmployeeDomain(scorecard.getAssessed()), ZonedDateTime.now(ZoneId.of("UTC")))));
                    if (!startCampaignEvents.isEmpty()) {
                        LOGGER.info("Send email startCampaignEvents");
                        try {
                            sendStartCampaignEmailEventListener.publishListWithParam(campaignEntity.getEndDate(), startCampaignEvents);
                        } catch (MessagingException | UnsupportedEncodingException e) {
                            LOGGER.error("Erreur lors de l'envoie de mail de démarrage de campagne", e);
                        }
                    }
                } else {
                    LOGGER.info("Status inProgress not found");
                }
            } else {
                LOGGER.info("La campagne est déjà en cours {} - {} - {}", campaignEntity.getName(), campaignEntity.getStatus().getName(), campaignEntity.getStartDate());
            }
        });
    }
}
