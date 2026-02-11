package com.cie.hr.application.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cie.hr.application.command.CloseCampaignCommand;
import com.cie.hr.application.command.CreateCampaignCommand;
import com.cie.hr.application.command.DeleteCampaignCommand;
import com.cie.hr.application.command.OpenCampaignCommand;
import com.cie.hr.application.command.UpdateCampaignCommand;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.utils.CheckRHEmployee;
import com.cie.hr.common.utils.ScorecardUtils;
import com.cie.hr.domain.entity.Campaign;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.entity.Profile;
import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.domain.entity.Status;
import com.cie.hr.domain.port.CampaignRepositoryPort;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.domain.port.StatusRepositoryPort;
import com.cie.hr.domain.usecase.CampaignUseCases;
import com.cie.hr.infrastructure.service.query.CampaignQuery;
import com.fasterxml.uuid.Generators;


@Component
public class CampaignUseCaseAdapter implements CampaignUseCases {

    private final CampaignRepositoryPort campaignRepositoryPort;
    private final StatusRepositoryPort statusRepositoryPort;
    private final ScoreCardRepositoryPort scoreCardRepositoryPort;
    private final JobRepositoryPort jobRepositoryPort;
    private final ScorecardUtils scorecardUtils;
    private final CheckRHEmployee checkRHEmployee;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final CampaignQuery campaignQuery;

    public CampaignUseCaseAdapter(CampaignRepositoryPort campaignRepositoryPort,
                                  StatusRepositoryPort statusRepositoryPort,
                                  ScoreCardRepositoryPort scoreCardRepositoryPort,
                                  JobRepositoryPort jobRepositoryPort,
                                  ScorecardUtils scorecardUtils,
                                  CheckRHEmployee checkRHEmployee,
                                  CampaignQuery campaignQuery) {
        this.campaignRepositoryPort = campaignRepositoryPort;
        this.statusRepositoryPort = statusRepositoryPort;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.jobRepositoryPort = jobRepositoryPort;
        this.scorecardUtils = scorecardUtils;
        this.checkRHEmployee = checkRHEmployee;
        this.campaignQuery = campaignQuery;
    }

    @Override
    public UUID createCampaign(CreateCampaignCommand command) {
        command.checkValidity();

        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
        }

        Optional<Status> status = statusRepositoryPort.findByCode("0");
        if (status.isEmpty()) {
            throw new ApplicationException("Ce status n'existe pas");
        }
        var campaign = new Campaign(Generators.timeBasedEpochGenerator().generate(), command.name(), command.start_date(), command.end_date(), status.get(), false);
        campaign.checkBusinessRules(campaignRepositoryPort);

        // Création des fiches de notation
        List<Job> jobs = jobRepositoryPort.findAllJobsWithEmployees();
        if (jobs.isEmpty()) {
            return campaign.getId();
        }

        List<ScorecardDomain> scorecardList = new ArrayList<>();
        jobs.stream().filter(job -> job.getParentId() != null && job.getEmployeeId() != null).forEach(job -> {
            EmployeeDomain employee = job.getEmployeeId();
            scorecardUtils.removeExistingScorecard(employee, campaign);
            Optional<Job> parentJob = jobRepositoryPort.findById(job.getParentId());
            if (parentJob.isEmpty()) {
                return;
            }
            Profile profile = employee.profile();
            boolean isManager = !profile.getCode().equals("CE");
            ScorecardDomain scorecard = isManager ? scorecardUtils.createScorecardForManager(job, campaign, employee, status.get()) : scorecardUtils.createScorecardForExpert(job, campaign, employee, status.get());
            if (scorecard != null) {
                scorecardList.add(scorecard);
            }
        });

        campaignRepositoryPort.save(campaign);
        scoreCardRepositoryPort.saveAll(scorecardList);

        // Retour de l'id de la campagne
        return campaign.getId();
    }

    @Override
    public UUID updateCampaign(UpdateCampaignCommand command) {
        command.checkValidity();

        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
        }

        var findCampaign = campaignRepositoryPort.findById(command.id());

        if (findCampaign.isEmpty()) {
            throw new ApplicationException("Cette campagne n'existe pas");
        }
        Campaign campaign = getCampaign(command, findCampaign.get());
        campaignRepositoryPort.updateAndSave(campaign);
        return campaign.getId();
    }

    private  @NotNull Campaign getCampaign(UpdateCampaignCommand command, Campaign campaign) {
        Status status = campaign.getStatus();
        switch (status.getCode()) {
            case "2" -> {
                var lastClosedCampaign = campaignRepositoryPort.findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc("2");
                if (lastClosedCampaign.isEmpty() || !lastClosedCampaign.get().getId().equals(campaign.getId())) {
                    throw new ApplicationException("Impossible de modifier une campagne clôturée");
                }
                campaign.setName(command.name());
                campaign.setEndDate(command.end_date());

                var checkIfOpenCampaignExist = campaignRepositoryPort.findFirstByStatusCode("1");
                if (checkIfOpenCampaignExist.isPresent()) {
                    throw new ApplicationException("Impossible de modifier une campagne clôturée alors qu'une autre campagne est en cours");
                }
                campaign.setStatus(statusRepositoryPort.findByCode("1").orElseThrow(() -> new ApplicationException("Erreur survenue lors de la modification de la campagne")));
            }
            case "1" -> {
                campaign.setName(command.name());
                campaign.setEndDate(command.end_date());
            }
            case "0" -> {
                campaign.setName(command.name());
                campaign.setStartDate(command.start_date());
                campaign.setEndDate(command.end_date());
            }
            default -> throw new ApplicationException("Cette opération n'est pas autorisée");
        }
        return campaign;
    }

    @Override
    public Boolean deleteCampaign(DeleteCampaignCommand command) {
        command.checkValidity();
        var findCampaign = campaignRepositoryPort.findById(command.campaignId());

        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
        }
        
        if (findCampaign.isEmpty()) {
            throw new ApplicationException("Cette campagne n'existe pas");
        }
        Campaign campaign = findCampaign.get();

        if (!campaign.getStatus().getCode().equals("1")) {
            throw new ApplicationException("Impossible de supprimer une campagne qui est déjà démarrée ou qui est clôturée");
        }
        List<ScorecardDomain> campaignScoreCards = scoreCardRepositoryPort.findByCampaignId(campaign.getId());
        campaignScoreCards.forEach(e -> e.setIsDeleted(true));

        scoreCardRepositoryPort.saveAll(campaignScoreCards);
        campaign.setDeleted(true);
        campaignRepositoryPort.updateAndSave(campaign);
        return true;
    }

    @Override
    public Boolean closeCampaign(CloseCampaignCommand command) {
        command.checkValidity();

        try {
            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
            }
            var campaign = campaignRepositoryPort.findById(command.campaignId());

            // Check if the campaign exists
            if (campaign.isEmpty()) {
                throw new ApplicationException("Cette campagne n'existe pas");
            }

            // Check if the campaign is already closed
            if (campaign.get().getStatus().getCode().equals("2")) {
                throw new ApplicationException("Cette campagne est déjà clôturée");
            }

            // Check if the campaign is not started
            if (campaign.get().getStatus().getCode().equals("0")) {
                throw new ApplicationException("Cette campagne n'est pas encore démarrée");
            }

            // Check if the campaign is not deleted
            if (campaign.get().isDeleted()) {
                throw new ApplicationException("Cette campagne a été supprimée");
            }

            // Close the campaign
            Optional<Status> status = statusRepositoryPort.findByCode("2");
            if (status.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }

            campaign.get().setStatus(status.get());
            campaignRepositoryPort.updateAndSave(campaign.get());
            return true;
        } catch (Exception e) {
            LOGGER.error("An error occurred while closing the campaign", e);
            throw new ApplicationException("Une erreur s'est produite lors de la clôture de la campagne");
        }
    }

    @Override
    public Boolean openCampaign(OpenCampaignCommand command) {
        command.checkValidity();
        try {
            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
            }

            var campaign = campaignRepositoryPort.findById(command.campaignId());

            if (campaign.isEmpty()) {
                throw new ApplicationException("Cette campagne n'existe pas");
            }

            if (campaign.get().getStatus().getCode().equals("1")) {
                throw new ApplicationException("Cette campagne est déjà démarrée");
            }

            if (campaign.get().isDeleted()) {
                throw new ApplicationException("Cette campagne a été supprimée");
            }

            Optional<Status> status = statusRepositoryPort.findByCode("1");
            if (status.isEmpty()) {
                throw new ApplicationException("Ce status n'existe pas");
            }

            // Clôturer les campagnes ouvertes s'ils en existent
            List<Campaign> openedCampaigns = campaignRepositoryPort.findByStatusCodeIn(Collections.singletonList("1"));

            if (!openedCampaigns.isEmpty()) {
                openedCampaigns.forEach(c -> {
                    c.setStatus(status.get());
                    campaignRepositoryPort.updateAndSave(c);
                });
            }

            campaign.get().setStatus(status.get());
            campaignRepositoryPort.updateAndSave(campaign.get());

            campaignQuery.startCampaign(campaign.get().getId());
            return true;
        } catch (Exception e) {
            LOGGER.error("An error occurred while opening the campaign", e);
            throw new ApplicationException("Une erreur s'est produite lors de l'ouverture de la campagne");
        }
    }


    @Override
    public int syncMissingScorecards() {
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette opération");
        }

        // Récupérer les campagnes actives (non démarrée ou en cours)
        List<String> codes = new ArrayList<>() {{
            add("0");
            add("1");
        }};
        List<Campaign> activeCampaigns = campaignRepositoryPort.findByStatusCodeIn(codes);

        if (activeCampaigns.isEmpty()) {
            LOGGER.info("Aucune campagne active trouvée pour la synchronisation des scorecards");
            return 0;
        }

        // Récupérer tous les jobs actifs avec un employé assigné
        List<Job> jobs = jobRepositoryPort.findAllJobsWithEmployees();
        if (jobs.isEmpty()) {
            LOGGER.info("Aucun poste avec employé trouvé");
            return 0;
        }

        int totalCreated = 0;

        for (Campaign campaign : activeCampaigns) {
            Optional<Status> status = statusRepositoryPort.findByCode("0");
            if (status.isEmpty()) {
                LOGGER.error("Statut 'notStarted' introuvable");
                continue;
            }

            List<ScorecardDomain> scorecardsToCreate = new ArrayList<>();

            for (Job job : jobs) {
                if (job.getEmployeeId() == null) {
                    continue;
                }

                EmployeeDomain employee = job.getEmployeeId();

                // Vérifier si un scorecard existe déjà pour cet employé dans cette campagne
                Optional<ScorecardDomain> existingScorecard = scoreCardRepositoryPort
                        .findByAssessed_IdAndCampaignId(employee.id(), campaign.getId());

                if (existingScorecard.isPresent()) {
                    continue;
                }

                // Créer le scorecard manquant
                Profile profile = employee.profile();
                boolean isManager = !profile.getCode().equals("CE");

                ScorecardDomain scorecard = isManager
                        ? scorecardUtils.createScorecardForManager(job, campaign, employee, status.get())
                        : scorecardUtils.createScorecardForExpert(job, campaign, employee, status.get());

                if (scorecard != null) {
                    // Assigner le manager si possible
                    if (job.getParentId() != null) {
                        Optional<Job> parentJob = jobRepositoryPort.findById(job.getParentId());
                        parentJob.ifPresent(parent -> {
                            if (parent.getEmployeeId() != null) {
                                scorecard.setManager(parent.getEmployeeId());
                            }
                        });
                    }

                    scorecardsToCreate.add(scorecard);
                    LOGGER.info("Scorecard manquant créé pour l'employé {} ({}) dans la campagne {}",
                            employee.firstname() + " " + employee.lastname(),
                            employee.email(),
                            campaign.getName());
                }
            }

            if (!scorecardsToCreate.isEmpty()) {
                scoreCardRepositoryPort.saveAll(scorecardsToCreate);
                totalCreated += scorecardsToCreate.size();
                LOGGER.info("{} scorecards créés pour la campagne {}", scorecardsToCreate.size(), campaign.getName());
            }
        }

        LOGGER.info("Synchronisation terminée : {} scorecards créés au total", totalCreated);
        return totalCreated;
    }

}
