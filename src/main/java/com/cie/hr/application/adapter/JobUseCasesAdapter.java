package com.cie.hr.application.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cie.hr.application.command.AssignJobToEmployeeCommand;
import com.cie.hr.application.command.CreateJobCommand;
import com.cie.hr.application.command.DeleteJobCommand;
import com.cie.hr.application.command.UpdateJobCommand;
import com.cie.hr.application.command.UpdateJobScorecard;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.utils.CheckRHEmployee;
import com.cie.hr.domain.entity.Campaign;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.Grade;
import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.entity.NoteDistribution;
import com.cie.hr.domain.entity.Organization;
import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.domain.port.CampaignRepositoryPort;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import com.cie.hr.domain.port.GradeRepositoryPort;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.domain.port.NoteDistributionRepositoryPort;
import com.cie.hr.domain.port.OrganizationRepositoryPort;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.domain.port.ScorecardExpertTemplateRepositoryPort;
import com.cie.hr.domain.port.ScorecardManagerTemplateRepositoryPort;
import com.cie.hr.domain.port.StatusRepositoryPort;
import com.cie.hr.domain.usecase.JobUseCases;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardTemplateImportVm;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import com.cie.hr.infrastructure.valueobject.FormSpecialLine;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import com.fasterxml.uuid.Generators;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */

@Component
public class JobUseCasesAdapter implements JobUseCases {

    private final OrganizationRepositoryPort organizationRepositoryPort;

    private final JobRepositoryPort jobRepositoryPort;

    private final EmployeeRepositoryPort employeeRepository;

    private final GradeRepositoryPort gradeRepositoryPort;

    private final CheckRHEmployee checkRHEmployee;

    private final ScoreCardRepositoryPort scoreCardRepositoryPort;

    private final CampaignRepositoryPort campaignRepositoryPort;

    private final ScorecardExpertTemplateRepositoryPort scorecardExpertTemplateRepositoryPort;

    private final ScorecardManagerTemplateRepositoryPort scorecardManagerTemplateRepositoryPort;

    private final StatusRepositoryPort statusRepositoryPort;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final NoteDistributionRepositoryPort noteDistributionRepositoryPort;

    public JobUseCasesAdapter(OrganizationRepositoryPort organizationRepositoryPort, JobRepositoryPort jobRepositoryPort,
                              EmployeeRepositoryPort employeeRepository, GradeRepositoryPort gradeRepositoryPort,
                              CheckRHEmployee checkRHEmployee, ScoreCardRepositoryPort scoreCardRepositoryPort,
                              CampaignRepositoryPort campaignRepositoryPort,
                              ScorecardExpertTemplateRepositoryPort scorecardExpertTemplateRepositoryPort,
                              ScorecardManagerTemplateRepositoryPort scorecardManagerTemplateRepositoryPort,
                              StatusRepositoryPort statusRepositoryPort,
                              NoteDistributionRepositoryPort noteDistributionRepositoryPort) {
        this.organizationRepositoryPort = organizationRepositoryPort;
        this.jobRepositoryPort = jobRepositoryPort;
        this.employeeRepository = employeeRepository;
        this.gradeRepositoryPort = gradeRepositoryPort;
        this.checkRHEmployee = checkRHEmployee;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.campaignRepositoryPort = campaignRepositoryPort;
        this.scorecardExpertTemplateRepositoryPort = scorecardExpertTemplateRepositoryPort;
        this.scorecardManagerTemplateRepositoryPort = scorecardManagerTemplateRepositoryPort;
        this.statusRepositoryPort = statusRepositoryPort;
        this.noteDistributionRepositoryPort = noteDistributionRepositoryPort;
    }

    @Override
    public UUID createJob(CreateJobCommand command) {
        command.checkValidity();

        // Check rights
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        LOGGER.info("Command : {}", command);

        EmployeeDomain employeeDomain;
        // Find employee
        if (command.employeeId() != null) {
            var employee = employeeRepository.findById(command.employeeId());

            if (employee.isEmpty())
                throw new ApplicationException("Cette employée n'existe pas");

            employeeDomain = employee.get();
        } else {
            employeeDomain = null;
        }

        // Find grade
        var grade = gradeRepositoryPort.findById(command.gradeId());

        if (grade.isEmpty())
            throw new ApplicationException("Ce grade n'existe pas");

        // Find Organization
        Optional<Organization> organization = organizationRepositoryPort.findById(command.organisationId());


        if (organization.isEmpty())
            throw new ApplicationException("Cette organisation n'existe pas");

        // Get Parent - utiliser la nouvelle méthode pour trouver le manager
        // qui remonte la hiérarchie organisationnelle
        Job parentJob = organizationRepositoryPort.findManagerForJob(organization.get().getId(), null);
        if (parentJob == null) {
            LOGGER.warn("Aucun manager trouvé pour l'organisation {}, parent_id sera null", organization.get().getId());
        }

        String finalGrade = grade.get().getCode();

        var newJob = Job.newBuilder()
                .title(command.title())
                .code(command.code())
                .grade(grade.get())
                .organizationId(organization.get())
                .employeeId(employeeDomain)
                .parentId(parentJob != null ? parentJob.getId() : null).build();

        newJob.checkBusinessRules(jobRepositoryPort, employeeRepository);

        List<String> codes = new ArrayList<>() {{
            add("0");
            add("1");
        }};
        var checkCampaign = campaignRepositoryPort.findByStatusCodeIn(codes);

        if (employeeDomain != null && !checkCampaign.isEmpty()) {
            checkCampaign.forEach(campaign -> {
                // Find if scorecard already exist
                var findScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employeeDomain.id(), campaign.getId());

                findScorecard.ifPresent(scoreCardRepositoryPort::delete);

                if (finalGrade.equals("CE")) {
                    var scorecardForExpert = scorecardExpertTemplateRepositoryPort.findFirstByTypeAndActiveTrue(2);
                    // Création de la fiche de l'employé
                    if (scorecardForExpert.isPresent()) {
                        var startStatus = statusRepositoryPort.findByCode("0");
                        if (startStatus.isEmpty()) {
                            throw new ApplicationException("Ce statut n'existe pas");
                        }
                        EvaluationScorecardExpert evaluationScorecardExpert = new EvaluationScorecardExpert(campaign.getId(), 0d, startStatus.get().getName(), scorecardForExpert.get());
                        ScorecardDomain scorecard = ScorecardDomain.newBuilder()
                                .id(Generators.timeBasedEpochGenerator().generate())
                                .campaign(campaign)
                                .status(startStatus.get())
                                .assessed(newJob.getEmployeeId())
                                .scorecardForManagerForm(null)
                                .scorecardForExpert(evaluationScorecardExpert)
                                .manager(null)
                                .build();
                        this.scoreCardRepositoryPort.save(scorecard);
                    } else {
                        LOGGER.info("Le fiche d'évaluation est introuvable pour cet expert");
                    }
                } else {
                    var scorecardTemplate = scorecardManagerTemplateRepositoryPort.findFirstByTypeAndActiveTrue(1);
                    // Création de la fiche de l'employé
                    if (scorecardTemplate.isPresent()) {
                        var startStatus = statusRepositoryPort.findByCode("0");
                        if (startStatus.isEmpty()) {
                            throw new ApplicationException("Ce statut n'existe pas");
                        }
                        EvaluationScorecardManager evaluationScorecardManager = new EvaluationScorecardManager(campaign.getId(), 0d, startStatus.get().getName(), scorecardTemplate.get());
                        ScorecardDomain scorecard = ScorecardDomain.newBuilder()
                                .id(Generators.timeBasedEpochGenerator().generate())
                                .campaign(campaign)
                                .status(startStatus.get())
                                .assessed(newJob.getEmployeeId())
                                .scorecardForExpert(null)
                                .scorecardForManagerForm(evaluationScorecardManager)
                                .manager(null)
                                .build();
                        this.scoreCardRepositoryPort.save(scorecard);
                    } else {
                        LOGGER.info("Le fiche d'évaluation est introuvable pour ce manager");
                    }
                }
            });
        }

        jobRepositoryPort.save(newJob);

        return newJob.getId();
    }

    @Override
    public UUID updateJob(UpdateJobCommand command) {
        command.checkValidity();

        // Check rights
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        // Find employee
        EmployeeDomain employeeDomain = null;

        CreateJobCommand job = command.job();

        // Trigger CI rebuild
        // Find employee
        if (job.employeeId() != null) {
            var employee = employeeRepository.findById(job.employeeId());

            if (employee.isEmpty())
                throw new ApplicationException("Cette employée n'existe pas");

            employeeDomain = employee.get();
        }

        Optional<Organization> currentOrganization = organizationRepositoryPort.findById(job.organisationId());
        Grade currentGrade = gradeRepositoryPort.findById(job.gradeId()).orElse(null);

        if (currentOrganization.isEmpty())
            throw new ApplicationException("Cette organisation n'existe pas");

        // Utiliser la nouvelle méthode pour trouver le manager
        // qui remonte la hiérarchie organisationnelle
        Job parentJob = organizationRepositoryPort.findManagerForJob(currentOrganization.get().getId(), command.jobId());
        if (parentJob == null) {
            LOGGER.warn("Aucun manager trouvé pour l'organisation {}, parent_id sera null", currentOrganization.get().getId());
        }

        LOGGER.info("Before start creating JobUpate");
        var jobUpdates = Job.newBuilder()
                .id(command.jobId())
                .title(job.title())
                .code(job.code())
                .organizationId(currentOrganization.get())
                .parentId(parentJob != null ? parentJob.getId() : null)
                .grade(currentGrade)
                .employeeId(employeeDomain)
                .build();

        LOGGER.info("Before start checking business rules");
        jobUpdates.checkBusinessRulesOnUpdate(jobRepositoryPort, employeeRepository);

        LOGGER.info("Before start updating job {}",jobUpdates);
        var jobRefreshed = jobRepositoryPort.findById(command.jobId()).map(e -> {
            e.setTitle(jobUpdates.getTitle());
            e.setCode(jobUpdates.getCode());
            e.setOrganizationId(jobUpdates.getOrganizationId());
            e.setParentId(jobUpdates.getParentId());
            e.setGrade(jobUpdates.getGrade());
            e.setEmployeeId(jobUpdates.getEmployeeId());
            return e;
        }).orElse(null);

        if (jobRefreshed == null) {
            LOGGER.info("Job not found {}", command.jobId());
            return null;
        } else {
            this.jobRepositoryPort.updateAndSave(jobRefreshed);
            
            // Mettre à jour le manager dans les scorecards de l'employé si le job a changé
            if (jobRefreshed.getEmployeeId() != null) {
                updateScorecardManager(jobRefreshed.getEmployeeId(), jobRefreshed);
            }
            
            // Recalculer les parent_id des postes dans les organisations enfants
            // si le poste modifié est potentiellement un chef (a un grade et un employé)
            if (jobRefreshed.getGrade() != null && jobRefreshed.getEmployeeId() != null) {
                recalculateChildJobsParent(jobRefreshed.getOrganizationId().getId());
            }

            // Ensure scorecard exists for the employee if added/modified during an active campaign
            if (jobRefreshed.getEmployeeId() != null) {
                createScorecardForEmployee(jobRefreshed.getEmployeeId(), jobRefreshed.getGrade().getCode());
            }
            
            return jobRefreshed.getId();
        }
    }

    @Override
    public Boolean deleteJob(DeleteJobCommand command) {

        // Check rights
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        var jobToDeleted = Job.newBuilder().id(command.id()).build();
        jobToDeleted.checkBusinessRulesOnDelete(jobRepositoryPort);

        var jobFromDb = jobRepositoryPort.findById(jobToDeleted.getId());
        if (jobFromDb.isPresent()) {
            jobFromDb.get().setDeleted(true);
            jobRepositoryPort.updateAndSave(jobFromDb.get());
            return true;
        }
        return false;
    }

    @Override
    public Boolean assignJobToEmployee(AssignJobToEmployeeCommand command) {
        command.checkValidity();

        // Check rights
        if (checkRHEmployee.employeeIsNotRH()) {
            throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
        }

        // Find job
        var job = jobRepositoryPort.findById(command.jobId());

        if (job.isEmpty()) {
            throw new ApplicationException("Ce poste n'existe pas");
        }

        Job jobDomain = job.get();
        EmployeeDomain currentEmployee = jobDomain.getEmployeeId();

        if (command.employeeId() == null) {
            if (currentEmployee == null) {
                throw new ApplicationException("Veuillez sélectionner un employée si vous souhaitez affecter ce poste");
            } else {
                jobDomain.setEmployeeId(null);
                deleteScorecard(currentEmployee.id());
                jobRepositoryPort.updateAndSave(jobDomain);
                return true;
            }
        } else {
            if (jobDomain.getEmployeeId() != null) {
                throw new ApplicationException("Ce poste est déjà occupé par " + jobDomain.getEmployeeId().firstname() + " " + jobDomain.getEmployeeId().lastname());
            }
            // Find employee
            var employee = employeeRepository.findById(command.employeeId());

            if (employee.isEmpty()) {
                throw new ApplicationException("Cet employée n'existe pas");
            }

            // Find employee Job
            Job findEmployeeJob = jobRepositoryPort.findByEmployeeId(employee.get().id()).orElse(null);
            if (findEmployeeJob != null) {
                // Libérer l'ancien poste
                findEmployeeJob.setEmployeeId(null);
                jobRepositoryPort.updateAndSave(findEmployeeJob);
                
                deleteScorecard(employee.get().id());
            }

            // Affecter au nouveau poste (toujours exécuté)
            jobDomain.setEmployeeId(employee.get());
            jobRepositoryPort.updateAndSave(jobDomain);
            createScorecardForEmployee(employee.get(), jobDomain.getGrade().getCode());
            
            // Mettre à jour le manager dans les scorecards actives de l'employé
            updateScorecardManager(employee.get(), jobDomain);
            
            // Recalculer les parent_id des postes dans les organisations enfants
            recalculateChildJobsParent(jobDomain.getOrganizationId().getId());
            
            return true;
        }
    }

    @Override
    public UUID updateJobScorecard(UpdateJobScorecard command) {
        try {
            command.checkValidity();

            // Check rights
            if (checkRHEmployee.employeeIsNotRH()) {
                throw new ApplicationException("Vous n'êtes pas autorisé à effectuer cette action");
            }

            // Find job
            Job job = jobRepositoryPort.findById(command.jobId()).orElse(null);

            if (job == null) {
                throw new ApplicationException("Ce poste n'existe pas");
            }

            boolean isExpert = job.getGrade().getCode().equals("CE");
            String title = isExpert ? "B- Objectifs opérationnels" : "D- Objectifs opérationnels";
            List<ScorecardTemplateImportVm> scorecardTemplateImportVms = getScorecardTemplate(command.file(), isExpert);

            FormSpecialSection formSpecialSection = getScorecardToSave(scorecardTemplateImportVms, title, isExpert);

            job.setJobTemplate(formSpecialSection);

            jobRepositoryPort.updateAndSave(job);

            return job.getId();
        } catch (Exception ex) {
            LOGGER.error("Erreur lors de la mise à jour de la fiche de notation", ex);
            throw new ApplicationException("Erreur lors de la mise à jour de la fiche de notation");
        }
    }

    private List<ScorecardTemplateImportVm> getScorecardTemplate(MultipartFile file, boolean isExpert) {
        String firstLetter = isExpert ? "B" : "D";
        List<ScorecardTemplateImportVm> scorecardTemplateImportVms = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheet("Contrat Objectifs");

            if (sheet == null) {
                throw new ApplicationException("Feuille 'Contrat Objectifs' non trouvée dans le fichier Excel");
            }

            int count = 1;
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                String indicator = getCellValueAsString(row.getCell(0));
                Double weight = getCellValueAsDouble(row.getCell(1));
                String unit = getCellValueAsString(row.getCell(2));
                Double targetValue = getCellValueAsDouble(row.getCell(3));
                String formulas = getCellValueAsString(row.getCell(4));

                if (indicator == null || unit == null || targetValue == null || weight == null || formulas == null) {
                    continue;
                }

                if (indicator.isEmpty() || unit.isEmpty() || formulas.isEmpty()) {
                    continue;
                }

                ScorecardTemplateImportVm scorecardTemplateImportVm = new ScorecardTemplateImportVm(
                        "%s%s- %s".formatted(firstLetter, count, indicator),
                        weight,
                        unit,
                        targetValue,
                        getCellValueAsString(row.getCell(4)),
                        getUnitFromName(formulas)
                );
                scorecardTemplateImportVms.add(scorecardTemplateImportVm);
                count++;
            }
            return scorecardTemplateImportVms;
        } catch (IOException | ApplicationException e) {
            throw new ApplicationException("Erreur lors de la lecture du fichier");
        }
    }

    // Méthodes utilitaires pour lire les valeurs des cellules
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        }
        return null;
    }

    private Double getCellValueAsDouble(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return cell.getNumericCellValue();
    }

    private FormSpecialSection getScorecardToSave(List<ScorecardTemplateImportVm> scorecardList, String title, boolean isExpert) {
        List<FormSpecialLine> formSpecialLines = scorecardList.stream().map(scorecard -> new FormSpecialLine(scorecard.indicator(), scorecard.weight(), 0d, scorecard.target(), 0d, scorecard.unit(), scorecard.unitId())).toList();
        return new FormSpecialSection(title, 0d, "normal", formSpecialLines, isExpert ? 0.85d : 0.5d, false);
    }

    private UUID getUnitFromName(String name) {
        return noteDistributionRepositoryPort.findByCode(name).map(NoteDistribution::getId).orElse(null);
    }

    private List<Campaign> getNotStartedAndRunningCampaign() {
        List<String> codes = new ArrayList<>() {{
            add("0");
            add("1");
        }};
        return campaignRepositoryPort.findByStatusCodeIn(codes);
    }

    private void deleteScorecard(UUID employeeId) {
        var checkCampaign = getNotStartedAndRunningCampaign();
        if (!checkCampaign.isEmpty()) {
            checkCampaign.forEach(campaign -> {
                // Check if ongoing evaluation
                var checkEvaluation = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employeeId, campaign.getId());
                checkEvaluation.ifPresent(scoreCardRepositoryPort::delete);
            });
        }
    }

    private void createScorecardForEmployee(EmployeeDomain employee, String grade) {
        var campaigns = getNotStartedAndRunningCampaign();
        campaigns.forEach(campaign -> {
            
            // Check if scorecard already exists to avoid duplicates
            var existingScorecard = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employee.id(), campaign.getId());
            if (existingScorecard.isPresent()) {
                LOGGER.info("Scorecard already exists for employee {} in campaign {}", employee.email(), campaign.getName());
                return;
            }

            if (grade.equals("CE")) {
                var scorecardForExpert = scorecardExpertTemplateRepositoryPort.findFirstByTypeAndActiveTrue(2);
                // Création de la fiche de l'employé
                if (scorecardForExpert.isPresent()) {
                    var startStatus = statusRepositoryPort.findByCode("0");
                    if (startStatus.isEmpty()) {
                        throw new ApplicationException("Ce statut n'existe pas");
                    }
                    EvaluationScorecardExpert evaluationScorecardExpert = new EvaluationScorecardExpert(campaign.getId(), 0d, startStatus.get().getName(), scorecardForExpert.get());
                    ScorecardDomain scorecard = ScorecardDomain.newBuilder()
                            .id(Generators.timeBasedEpochGenerator().generate())
                            .campaign(campaign)
                            .status(startStatus.get())
                            .assessed(employee)
                            .scorecardForManagerForm(null)
                            .scorecardForExpert(evaluationScorecardExpert)
                            .manager(null)
                            .build();
                    this.scoreCardRepositoryPort.save(scorecard);
                }
            } else {
                var scorecardTemplate = scorecardManagerTemplateRepositoryPort.findFirstByTypeAndActiveTrue(1);
                // Création de la fiche de l'employé
                if (scorecardTemplate.isPresent()) {
                    var startStatus = statusRepositoryPort.findByCode("0");
                    if (startStatus.isEmpty()) {
                        throw new ApplicationException("Ce statut n'existe pas");
                    }
                    EvaluationScorecardManager evaluationScorecardManager = new EvaluationScorecardManager(campaign.getId(), 0d, startStatus.get().getName(), scorecardTemplate.get());
                    ScorecardDomain scorecard = ScorecardDomain.newBuilder()
                            .id(Generators.timeBasedEpochGenerator().generate())
                            .campaign(campaign)
                            .status(startStatus.get())
                            .assessed(employee)
                            .scorecardForExpert(null)
                            .scorecardForManagerForm(evaluationScorecardManager)
                            .manager(null)
                            .build();
                    this.scoreCardRepositoryPort.save(scorecard);
                }
            }
        });
    }

    /**
     * Met à jour le manager des scorecards pour l'employé donné.
     * Le manager est déterminé en cherchant le chef de l'organisation parente.
     * 
     * @param employee L'employé dont les scorecards doivent être mises à jour
     * @param job Le poste de l'employé
     */
    private void updateScorecardManager(EmployeeDomain employee, Job job) {
        var campaigns = getNotStartedAndRunningCampaign();
        
        for (Campaign campaign : campaigns) {
            var scorecardOpt = scoreCardRepositoryPort.findByAssessed_IdAndCampaignId(employee.id(), campaign.getId());
            
            if (scorecardOpt.isPresent()) {
                ScorecardDomain scorecard = scorecardOpt.get();
                
                // Trouver le manager via l'organisation parente
                Job managerJob = organizationRepositoryPort.findManagerForJob(
                    job.getOrganizationId().getId(), 
                    job.getId()
                );
                
                // Protection : Ne mettre à jour que si le statut est "0" (Not Started)
                // Les évaluations commencées (1) ou terminées (2, 3) conservent leur manager
                if (scorecard.status() != null && "0".equals(scorecard.status().getCode())) {
                    if (managerJob != null && managerJob.getEmployeeId() != null) {
                        scorecard.setManager(managerJob.getEmployeeId());
                        scoreCardRepositoryPort.updateAndSave(scorecard);
                        LOGGER.info("Manager mis à jour pour le scorecard de {} : {}", 
                            employee.email(), managerJob.getEmployeeId().email());
                    }
                } else {
                    String statusCode = scorecard.status() != null ? scorecard.status().getCode() : "NULL";
                    LOGGER.info("Scorecard {} statut {}, modification manager ignorée", scorecard.id(), statusCode);
                }
            }
        }
    }

    /**
     * Recalcule automatiquement le parent_id de tous les postes dans les organisations enfants
     * lorsque le chef de l'organisation actuelle change.
     * 
     * @param organizationId L'ID de l'organisation dont le chef a potentiellement changé
     */
    private void recalculateChildJobsParent(UUID organizationId) {
        LOGGER.info("Recalcul des parent_id pour les enfants de l'organisation {}", organizationId);
        
        // 1. Récupérer le nouveau chef de l'organisation actuelle
        Job chiefJob = organizationRepositoryPort.findChiefJob(organizationId);
        UUID newParentId = chiefJob != null ? chiefJob.getId() : null;
        
        LOGGER.info("Nouveau chef trouvé: {}", newParentId != null ? newParentId : "AUCUN");
        
        // 2. Récupérer toutes les organisations enfants directes
        List<Organization> childOrganizations = organizationRepositoryPort.findByParentId(organizationId);
        
        if (childOrganizations.isEmpty()) {
            LOGGER.info("Aucune organisation enfant trouvée pour {}", organizationId);
            return;
        }
        
        LOGGER.info("Nombre d'organisations enfants trouvées: {}", childOrganizations.size());
        
        // 3. Pour chaque organisation enfant, mettre à jour tous ses postes
        int totalUpdated = 0;
        for (Organization childOrg : childOrganizations) {
            List<Job> jobsToUpdate = jobRepositoryPort.findByOrganizationId(childOrg.getId());
            
            for (Job job : jobsToUpdate) {
                // Force le parent selon la hiérarchie organisationnelle
                if (!java.util.Objects.equals(job.getParentId(), newParentId)) {
                    job.setParentId(newParentId);
                    jobRepositoryPort.updateAndSave(job);
                    
                    // Propager le changement de manager aux scorecards de l'employé
                    if (job.getEmployeeId() != null) {
                        updateScorecardManager(job.getEmployeeId(), job);
                    }
                    
                    totalUpdated++;
                    LOGGER.debug("Mis à jour parent_id pour job {} vers {}", job.getId(), newParentId);
                }
            }
        }
        
        LOGGER.info("Recalcul terminé: {} postes mis à jour", totalUpdated);
    }
}
