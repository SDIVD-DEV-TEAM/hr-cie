package com.cie.hr.common.scheduler;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.cie.hr.common.event.ScorecardEvent;
import com.cie.hr.common.event.StartCampaignEvent;
import com.cie.hr.common.event.listeners.ScorecardEventListener;
import com.cie.hr.common.event.listeners.SendCloseCampaignEmailEventListener;
import com.cie.hr.common.security.service.LoginAttemptService;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.DerogationEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEmbeddedEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.entity.ScorecardExpertTemplateEntity;
import com.cie.hr.infrastructure.entity.ScorecardManagerTemplateEntity;
import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.mapper.ScorecardMapper;
import com.cie.hr.infrastructure.repository.CampaignJpaRepository;
import com.cie.hr.infrastructure.repository.DerogationJpaRepository;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardExpertTemplateJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardManagerTemplateJpaRepository;
import com.cie.hr.infrastructure.repository.StatusJpaRepository;
import com.cie.hr.infrastructure.service.AsyncEmailBatchService;
import com.cie.hr.infrastructure.service.query.CampaignQuery;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import com.cie.hr.infrastructure.valueobject.FormSpecialLine;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;

import jakarta.mail.MessagingException;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private final CampaignJpaRepository campaignJpaRepository;
    private final ScorecardJpaRepository scorecardJpaRepository;
    private final StatusJpaRepository statusJpaRepository;
    private final EmployeeJpaRepository employeeJpaRepository;
    private final JobJpaRepository jobJpaRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final SendCloseCampaignEmailEventListener sendCloseCampaignEmailEventListener;
    private final ScorecardEventListener scorecardEventListener;
    private final DerogationJpaRepository derogationJpaRepository;
    private final CampaignQuery campaignQuery;
    private final AsyncEmailBatchService asyncEmailBatchService;
    private final LoginAttemptService loginAttemptService;
    private final ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository;
    private final ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository;

    public ScheduledTasks(CampaignJpaRepository campaignJpaRepository,
                          ScorecardJpaRepository scorecardJpaRepository,
                          StatusJpaRepository statusJpaRepository,
                          EmployeeJpaRepository employeeJpaRepository,
                          JobJpaRepository jobJpaRepository,
                          DerogationJpaRepository derogationJpaRepository,
                          SendCloseCampaignEmailEventListener sendCloseCampaignEmailEventListener,
                          ScorecardEventListener scorecardEventListener, CampaignQuery campaignQuery,
                          AsyncEmailBatchService asyncEmailBatchService,
                          LoginAttemptService loginAttemptService,
                          ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository,
                          ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository) {
        this.campaignJpaRepository = campaignJpaRepository;
        this.scorecardJpaRepository = scorecardJpaRepository;
        this.statusJpaRepository = statusJpaRepository;
        this.employeeJpaRepository = employeeJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
        this.sendCloseCampaignEmailEventListener = sendCloseCampaignEmailEventListener;
        this.scorecardEventListener = scorecardEventListener;
        this.derogationJpaRepository = derogationJpaRepository;
        this.campaignQuery = campaignQuery;
        this.asyncEmailBatchService = asyncEmailBatchService;
        this.loginAttemptService = loginAttemptService;
        this.scorecardManagerTemplateJpaRepository = scorecardManagerTemplateJpaRepository;
        this.scorecardExpertTemplateJpaRepository = scorecardExpertTemplateJpaRepository;
    }

    // Scheduled cron every day at 00:00
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleTaskForDerogationStatusChanges() {
        LOGGER.info("Start scheduler Derogation");
        // All derogation with endDate >= Today
        List<DerogationEntity> derogations = derogationJpaRepository.findAllByExpiredAtBefore(LocalDate.now());
        if (!derogations.isEmpty()) {
            derogations.forEach(derogation -> {
                derogation.setDeleted(true);
                derogationJpaRepository.save(derogation);
            });
        }
    }

    @Scheduled(cron = "0 30 7 * * ?")
    public void scheduleTaskForCampaignStatusChanges() {
        LOGGER.info("Start scheduler");
        // All campaign with endDate >= Today
        Optional<CampaignEntity> campaignNotStartedEntity = campaignJpaRepository.findFirstByEndDateLessThanEqualAndStatusCodeNot(new Date(), "2");
        if (campaignNotStartedEntity.isPresent()) {
            LOGGER.info("Find Campaign to close");
            var statusNotStartedEntity = statusJpaRepository.findByCode("2");
            if (statusNotStartedEntity.isPresent()) {
                LOGGER.info("Find Closed Status");
                campaignNotStartedEntity.get().setStatus(statusNotStartedEntity.get());
                campaignJpaRepository.save(campaignNotStartedEntity.get());
            }
        }

        // open all campaign
        Optional<CampaignEntity> campaignToOpen = campaignJpaRepository.findFirstByStartDateLessThanEqualAndStatusCodeOrderByStartDateDesc(new Date(), "0");
        campaignToOpen.ifPresent(campaign -> campaignQuery.startCampaign(campaign.getId()));

        // close all campaign with endDate < Today
        var statusCloseEntity = statusJpaRepository.findByCode("2");
        if (statusCloseEntity.isPresent()) {
            LOGGER.info("Find Closed Status");
            var today = new Date();
            LOGGER.info("Date {}", today.toInstant().toString());
            var campaignToClose = campaignJpaRepository.findByEndDateLessThanEqualAndStatusCode(today, "1").stream().peek(e -> e.setStatus(statusCloseEntity.get())).toList();
            // if campaignToClose > 0 ==> send an email
            if (!campaignToClose.isEmpty()) {
                campaignJpaRepository.saveAll(campaignToClose);
            } else {
                LOGGER.info("No campaign to close");
            }

            campaignToClose.forEach(campaign -> {
                List<StartCampaignEvent> startCampaignEvents = new ArrayList<>();
                List<ScorecardEntity> scorecardEntityList = scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaign.getId());
                scorecardEntityList.forEach(scorecard -> startCampaignEvents.add(new StartCampaignEvent(EmployeeMapper.toEmployeeDomain(scorecard.getAssessed()), ZonedDateTime.now(ZoneId.of("UTC")))));
                if (!startCampaignEvents.isEmpty()) {
                    LOGGER.info("Send email closeCampaignEvents");
                    try {
                        sendCloseCampaignEmailEventListener.publishListWithParam(campaign.getStartDate(), startCampaignEvents);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        LOGGER.error("Error when sending email to", e);
                    }
                }
            });
        }
    }

    @Scheduled(cron = "0 30 7 * * ?")
    public void scheduleTaskForScorecardStatusChanges() {
        // all validate scorecard
        var scorecards = scorecardJpaRepository.findByStatusNameAndEndDateDaysBefore("evaluated");
        scorecards.forEach(scorecard -> {
            Optional<EmployeeEntity> employee = employeeJpaRepository.findById(scorecard.getAssessed().getId());
            if (employee.isPresent()) {
                Optional<StatusEntity> status = statusJpaRepository.findByCode("2");
                if (status.isPresent()) {
                    scorecard.setStatus(status.get());
                    scorecard.setAutomaticClosed(true);
                    scorecardJpaRepository.save(scorecard);
                    ScorecardDomain scorecardDomain = ScorecardMapper.toDomain(scorecard);
                    var event = new ScorecardEvent(scorecardDomain.assessed(), ZonedDateTime.now());
                    try {
                        scorecardEventListener.publishOnClose(event);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        LOGGER.error(e.getMessage());
                    }
                }
            }
        });
    }

    @Scheduled(cron = "0 30 9 * * ?")
    public void scheduleTaskForScorecardNotEvaluated() {
        List<String> status = List.of("inProgress", "notStarted", "dispute");
        var campaign = campaignJpaRepository.findFirstByStatusCode("1");
        if (campaign.isPresent()) {
            Date endDate = campaign.get().getEndDate();
            Date today = new Date();
            long daysDifference = ChronoUnit.DAYS.between(today.toInstant(), endDate.toInstant());
            if (daysDifference <= 7) {
                var scorecards = scorecardJpaRepository.findAllByDeletedFalseAndCampaignIdAndStatusNameIn(campaign.get().getId(), status);
                List<String> managerEmail = new ArrayList<>();
                scorecards.forEach(scorecard -> {
                    ScorecardDomain scorecardDomain = ScorecardMapper.toDomain(scorecard);
                    ScorecardEvent event;
                    if (scorecardDomain.getManager() == null) {
                        JobEntity findEmployeeJob = jobJpaRepository.findByEmployeeId(scorecard.getAssessed().getId()).orElse(null);
                        if (findEmployeeJob != null) {
                            EmployeeEntity manager = findEmployeeJob.getParent() == null ? null : findEmployeeJob.getParent().getEmployee();
                            if (manager != null) {
                                if (managerEmail.isEmpty()) {
                                    EmployeeDomain employeeDomain = EmployeeMapper.toEmployeeDomain(manager);
                                    managerEmail.add(manager.getEmail());
                                    event = new ScorecardEvent(employeeDomain, ZonedDateTime.now());
                                } else if (managerEmail.stream().filter(e -> e.equals(manager.getEmail())).toList().isEmpty()) {
                                    EmployeeDomain employeeDomain = EmployeeMapper.toEmployeeDomain(manager);
                                    event = new ScorecardEvent(employeeDomain, ZonedDateTime.now());
                                    managerEmail.add(manager.getEmail());
                                } else event = null;
                            } else event = null;
                        } else event = null;
                    } else {
                        if (managerEmail.stream().filter(e -> e.equals(scorecardDomain.getManager().email())).toList().isEmpty()) {
                            managerEmail.add(scorecardDomain.getManager().email());
                            event = new ScorecardEvent(scorecardDomain.getManager(), ZonedDateTime.now());
                        } else event = null;
                    }
                    if (event != null) {
                        LOGGER.info("Send email notEvaluated");
                        try {
                            scorecardEventListener.publishOnNotEvaluated(event, Long.toString(daysDifference));
                        } catch (MessagingException | UnsupportedEncodingException e) {
                            LOGGER.error("Error when sending email");
                            LOGGER.error(e.getMessage());
                        }
                    }
                });
            }
        }
    }

    /**
     * Scheduled task to update scorecards with null manager
     * Runs every day at 02:00 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduleTaskForScorecardManagerUpdate() {
        LOGGER.info("Starting scheduled task: Scorecard Manager Update");
        
        // Find all scorecards in active campaigns with null manager
        Optional<CampaignEntity> activeCampaign = campaignJpaRepository.findFirstByStatusCode("1");
        
        if (activeCampaign.isEmpty()) {
            LOGGER.info("No active campaign found, skipping manager update");
            return;
        }
        
        List<ScorecardEntity> scorecardsWithNullManager = scorecardJpaRepository
            .findByDeletedFalseAndCampaignId(activeCampaign.get().getId())
            .stream()
            .filter(scorecard -> scorecard.getManager() == null)
            .toList();
        
        if (scorecardsWithNullManager.isEmpty()) {
            LOGGER.info("No scorecards with null manager found");
            return;
        }
        
        LOGGER.info("Found {} scorecards with null manager", scorecardsWithNullManager.size());
        int updatedCount = 0;
        
        for (ScorecardEntity scorecard : scorecardsWithNullManager) {
            try {
                // Find the job of the assessed employee (most recent active job)
                Optional<JobEntity> employeeJob = jobJpaRepository
                    .findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(scorecard.getAssessed().getId());
                
                if (employeeJob.isEmpty()) {
                    LOGGER.warn("No job found for employee {}", scorecard.getAssessed().getEmployeeNumber());
                    continue;
                }
                
                // Remonter la hiérarchie organisationnelle pour trouver le manager
                EmployeeEntity manager = findManagerByOrganizationHierarchy(employeeJob.get());
                
                if (manager == null) {
                    LOGGER.info("No manager found for employee {} in organization hierarchy", 
                        scorecard.getAssessed().getEmployeeNumber());
                    continue;
                }
                
                // Update the scorecard with the manager
                scorecard.setManager(manager);
                scorecardJpaRepository.save(scorecard);
                updatedCount++;
                
                LOGGER.info("Updated scorecard for employee {} with manager {}", 
                    scorecard.getAssessed().getEmployeeNumber(), 
                    manager.getEmployeeNumber());
                
            } catch (Exception e) {
                LOGGER.error("Error updating manager for scorecard {}: {}", 
                    scorecard.getId(), e.getMessage(), e);
            }
        }
        
        LOGGER.info("Scorecard Manager Update completed: {} scorecards updated", updatedCount);
    }

    /**
     * Scheduled task to manage user accounts:
     * 1. Send credentials emails to users who haven't received them yet
     * 2. Unlock blocked users
     * Runs every 10 minutes
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void scheduleTaskForUserAccountManagement() {
        LOGGER.info("Start scheduler: User Account Management");
        
        // Part 1: Send credentials emails to users who haven't received them
        List<EmployeeEntity> usersWithoutEmails = employeeJpaRepository.findActiveUsersWithoutCredentialsEmail();
        
        if (!usersWithoutEmails.isEmpty()) {
            LOGGER.info("Found {} active users without credentials email", usersWithoutEmails.size());
            
            try {
                asyncEmailBatchService.sendCredentialsEmailsAsync(usersWithoutEmails)
                    .thenAccept(result -> {
                        LOGGER.info("Credentials email batch completed: {} sent, {} failed ({}% success rate)",
                            result.successCount(),
                            result.failureCount(),
                            String.format("%.2f", result.successRate()));
                        
                        if (result.hasFailures()) {
                            LOGGER.warn("Failed to send credentials email to: {}", 
                                String.join(", ", result.failedEmployees()));
                        }
                    })
                    .exceptionally(ex -> {
                        LOGGER.error("Error during credentials email batch: {}", ex.getMessage(), ex);
                        return null;
                    });
                    
            } catch (Exception e) {
                LOGGER.error("Error initiating credentials email batch: {}", e.getMessage(), e);
            }
        } else {
            LOGGER.info("No users without credentials email found");
        }
        
        // Part 2: Unlock blocked users
        List<EmployeeEntity> lockedUsers = employeeJpaRepository.findLockedUsers();
        
        if (!lockedUsers.isEmpty()) {
            LOGGER.info("Found {} locked users to unlock", lockedUsers.size());
            int unlockedCount = 0;
            
            for (EmployeeEntity user : lockedUsers) {
                try {
                    // Clear the login attempt cache
                    loginAttemptService.evictUserFromLoginAttemptCache(user.getEmail());
                    
                    // Unlock the user
                    user.setIsNotLocked(true);
                    user.setFailedAttempts(0);
                    user.setLastFailedAttempt(null);
                    employeeJpaRepository.save(user);
                    
                    unlockedCount++;
                    LOGGER.info("Unlocked user: {} - {} ({})", 
                        user.getEmployeeNumber(), 
                        user.getFullName(), 
                        user.getEmail());
                        
                } catch (Exception e) {
                    LOGGER.error("Error unlocking user {}: {}", 
                        user.getEmail(), e.getMessage(), e);
                }
            }
            
            LOGGER.info("User unlock completed: {} users unlocked", unlockedCount);
        } else {
            LOGGER.info("No locked users found");
        }
        
        LOGGER.info("User Account Management scheduler completed");
    }

    /**
     * Remonte la hiérarchie organisationnelle pour trouver un manager.
     * Cherche le chef de l'organisation parente qui a un employé assigné.
     * 
     * @param job Le poste de l'employé
     * @return L'employé manager ou null si aucun trouvé
     */
    private EmployeeEntity findManagerByOrganizationHierarchy(JobEntity job) {
        if (job.getOrganization() == null) {
            return null;
        }
        
        OrganizationEntity parent = job.getOrganization().getParent();
        
        // Remonter la hiérarchie jusqu'à trouver un chef avec un employé assigné
        while (parent != null) {
            JobEntity chiefJob = parent.getChiefJob();
            
            if (chiefJob != null && chiefJob.getEmployee() != null 
                && !chiefJob.getId().equals(job.getId())) {
                LOGGER.info("Manager trouvé pour {} : {} (chef de {})", 
                    job.getTitle(), chiefJob.getEmployee().getFullName(), parent.getName());
                return chiefJob.getEmployee();
            }
            
            // Remonter d'un niveau
            parent = parent.getParent();
        }
        
        return null;
    }

    /**
     * Exécuter la synchronisation des scorecards manquants au démarrage de l'application.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        LOGGER.info("Application démarrée - Lancement des tâches initiales");
        cleanupInvalidScorecards();
        scheduleTaskForMissingScorecards();
    }


    /**
     * Nettoyer les scorecards invalides (sans templates) créés par erreur.
     */
    public void cleanupInvalidScorecards() {
        LOGGER.info("Start cleanup: Invalid Scorecards (missing templates)");
        try {
            List<ScorecardEntity> invalidScorecards = scorecardJpaRepository.findInvalidScorecards();
            
            if (!invalidScorecards.isEmpty()) {
                LOGGER.info("Found {} invalid scorecards to delete", invalidScorecards.size());
                scorecardJpaRepository.deleteAllInBatch(invalidScorecards);
                LOGGER.info("Cleanup completed: {} scorecards deleted", invalidScorecards.size());
            } else {
                LOGGER.info("No invalid scorecards found");
            }
        } catch (Exception e) {
            LOGGER.error("Error during invalid scorecards cleanup", e);
        }
    }

    /**
     * Tâche planifiée pour détecter et créer les scorecards manquants.
     * S'exécute chaque jour à 03:00 AM.
     * Détecte les employés avec un poste actif mais sans scorecard dans la campagne active.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduleTaskForMissingScorecards() {
        LOGGER.info("Start scheduler: Missing Scorecards Detection");
        
        try {
            Optional<CampaignEntity> activeCampaign = campaignJpaRepository.findFirstByStatusCode("1");
            
            if (activeCampaign.isEmpty()) {
                LOGGER.info("No active campaign found, skipping missing scorecards detection");
                return;
            }
            
            // Récupérer tous les jobs actifs avec un employé assigné
            List<JobEntity> jobsWithEmployees = jobJpaRepository.findAllByDeletedFalseAndEmployeeIdIsNotNull();
            
            if (jobsWithEmployees.isEmpty()) {
                LOGGER.info("No active jobs with employees found");
                return;
            }
            
            Optional<StatusEntity> notStartedStatus = statusJpaRepository.findByCode("0");
            if (notStartedStatus.isEmpty()) {
                LOGGER.error("Status 'notStarted' not found");
                return;
            }
            
            int createdCount = 0;
            CampaignEntity campaign = activeCampaign.get();
            
            
            for (JobEntity job : jobsWithEmployees) {
                if (processMissingScorecardForJob(job, campaign, notStartedStatus.get())) {
                    createdCount++;
                }
            }
            
            LOGGER.info("Missing Scorecards Detection completed: {} scorecards created", createdCount);
            
        } catch (Exception e) {
            LOGGER.error("Error during missing scorecards detection", e);
        }
    }

    private boolean processMissingScorecardForJob(JobEntity job, CampaignEntity campaign, StatusEntity notStartedStatus) {
        EmployeeEntity employee = job.getEmployee();
        if (employee == null || employee.isDeleted()) {
            return false;
        }

        // Vérifier si un scorecard existe déjà
        Optional<ScorecardEntity> existingScorecard = scorecardJpaRepository
                .findByDeletedFalseAndAssessedIdAndCampaignId(employee.getId(), campaign.getId());

        if (existingScorecard.isPresent()) {
            return false;
        }

        // Check if DG
        if (job.getGrade() != null && ("DG".equalsIgnoreCase(job.getGrade().getCode()) || job.getGrade().getRank() != null && job.getGrade().getRank() == 0)) {
            LOGGER.info("Skipping DG: Scorecard creation for: {}", employee.getEmail());
            return false;
        }

        // Assigner le manager via la hiérarchie organisationnelle
        EmployeeEntity manager = findManagerByOrganizationHierarchy(job);
        if (manager == null) {
            LOGGER.warn("SKIPPING: No manager found for employee {} ({}) - Cannot create scorecard",
                    employee.getFullName(), employee.getEmail());
            return false;
        }

        // Créer le scorecard manquant
        boolean isExpert = job.getGrade() != null && "CE".equals(job.getGrade().getCode());

        ScorecardEntity newScorecard = ScorecardEntity.builder()
                .campaign(campaign)
                .assessed(employee)
                .status(notStartedStatus)
                .automaticClosed(false)
                .build();
        newScorecard.setManager(manager);

        // Copier les infos du job
        if (job.getOrganization() != null && job.getGrade() != null) {
            JobEmbeddedEntity jobEmbedded = new JobEmbeddedEntity(
                    job.getTitle(),
                    job.getCode(),
                    job.getOrganization().getName(),
                    job.getGrade().getName(),
                    job.getOrganization().getType() != null
                            ? job.getOrganization().getType().getName() : null
            );
            newScorecard.setJob(jobEmbedded);
        }

        // Créer le template approprié (manager ou expert)
        if (isExpert) {
            Optional<ScorecardExpertTemplateEntity> expertTemplate = scorecardExpertTemplateJpaRepository.findFirstByTypeAndActiveTrue(2);
            if (expertTemplate.isPresent()) {
                ScorecardForExpert expertForm = getScorecardForExpert(job, expertTemplate.get());
                EvaluationScorecardExpert evaluationScorecardExpert = new EvaluationScorecardExpert(campaign.getId(), 0d, notStartedStatus.getName(), expertForm);
                newScorecard.setExpertTemplate(evaluationScorecardExpert);
                LOGGER.info("Creating expert scorecard for employee {} ({})",
                        employee.getFullName(), employee.getEmail());
            } else {
                LOGGER.error("No active expert template found for employee {}", employee.getEmail());
                return false; // Skip if no template found
            }
        } else {
            Optional<ScorecardManagerTemplateEntity> managerTemplate = scorecardManagerTemplateJpaRepository.findFirstByTypeAndActiveTrue(1);
            if (managerTemplate.isPresent()) {
                ScorecardForManagerForm managerForm = getScorecardForManagerForm(job, managerTemplate.get());
                EvaluationScorecardManager evaluationScorecardManager = new EvaluationScorecardManager(campaign.getId(), 0d, notStartedStatus.getName(), managerForm);
                newScorecard.setManagerTemplate(evaluationScorecardManager);
                LOGGER.info("Creating manager scorecard for employee {} ({})",
                        employee.getFullName(), employee.getEmail());
            } else {
                LOGGER.error("No active manager template found for employee {}", employee.getEmail());
                return false; // Skip if no template found
            }
        }

        newScorecard.setId(com.fasterxml.uuid.Generators.timeBasedEpochGenerator().generate());
        scorecardJpaRepository.save(newScorecard);
        return true;
    }

    private ScorecardForExpert getScorecardForExpert(JobEntity job, ScorecardExpertTemplateEntity scorecardForExpert) {
        ScorecardForExpert expertForm = scorecardForExpert.getValues();
        FormSpecialSection specialSection = expertForm.sectionB();

        if (job.getJobTemplate() != null) {
            List<FormSpecialLine> jobLines = job.getJobTemplate().lines();
            FormSpecialSection section = new FormSpecialSection(specialSection.title(), specialSection.note(), specialSection.type(), jobLines, specialSection.coefficient(), false);
            expertForm = new ScorecardForExpert(expertForm.sectionA(), section, expertForm.sectionC(), expertForm.sectionD());
        }
        return expertForm;
    }

    private ScorecardForManagerForm getScorecardForManagerForm(JobEntity job, ScorecardManagerTemplateEntity scorecardTemplate) {
        ScorecardForManagerForm formManager = scorecardTemplate.getValues();
        FormSpecialSection specialSection = formManager.sectionD();

        if (job.getJobTemplate() != null) {
            List<FormSpecialLine> jobLines = job.getJobTemplate().lines();
            FormSpecialSection section = new FormSpecialSection(specialSection.title(), specialSection.note(), specialSection.type(), jobLines, specialSection.coefficient(), false);
            formManager = new ScorecardForManagerForm(formManager.sectionA(), formManager.sectionB(), formManager.sectionC(), section, formManager.sectionE(), formManager.sectionF());
        }
        return formManager;
    }
}

