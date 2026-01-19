package com.cie.hr.infrastructure.service.query;

import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.StartCampaignEvent;
import com.cie.hr.common.event.listeners.SendStartCampaignEmailEventListener;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
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
import com.cie.hr.infrastructure.service.viewmodel.CampaignVm;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class CampaignQuery {

    private final CampaignJpaRepository campaignJpaRepository;
    private final EmployeeJpaRepository employeeJpaRepository;
    private final CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher;
    private final StatusJpaRepository statusJpaRepository;
    private final ScorecardJpaRepository scorecardJpaRepository;
    private final SendStartCampaignEmailEventListener sendStartCampaignEmailEventListener;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public CampaignQuery(CampaignJpaRepository campaignJpaRepository,
                         EmployeeJpaRepository employeeJpaRepository,
                         CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher,
                         StatusJpaRepository statusJpaRepository,
                         ScorecardJpaRepository scorecardJpaRepository,
                         SendStartCampaignEmailEventListener sendStartCampaignEmailEventListener) {
        this.campaignJpaRepository = campaignJpaRepository;
        this.employeeJpaRepository = employeeJpaRepository;
        this.createEmployeeRequestMessagePublisher = createEmployeeRequestMessagePublisher;
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
            List<EmployeeEntity> employees = employeeJpaRepository.findBySendAccountIdEmail(false);
            LOGGER.info("Send email createEmployeeEvent size {}", employees.size());
            employees.forEach(employee -> {
                try {
                    LOGGER.info("Send email createEmployeeEvent start");
                    var event = new CreateEmployeeEvent(EmployeeMapper.toEmployeeDomain(employee), ZonedDateTime.now(ZoneId.of("UTC")));
                    createEmployeeRequestMessagePublisher.publish(event);
                    LOGGER.info("Send email createEmployeeEvent end");
                    employee.setSendAccountIdEmail(true);
                    employeeJpaRepository.save(employee);
                    LOGGER.info("save employee end");
                } catch (MessagingException | UnsupportedEncodingException e) {
                    LOGGER.error("Erreur lors de l'envoie de mail de démarrage de campagne", e);
                }
            });

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
