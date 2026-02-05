package com.cie.hr.infrastructure.service.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.InfrastructureException;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.DelegationEntity;
import com.cie.hr.infrastructure.entity.DerogationEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.entity.ScoreRangeEntity;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.mapper.DisputesMapper;
import com.cie.hr.infrastructure.repository.CampaignJpaRepository;
import com.cie.hr.infrastructure.repository.DelegationJpaRepository;
import com.cie.hr.infrastructure.repository.DerogationJpaRepository;
import com.cie.hr.infrastructure.repository.DisputesJpaRepository;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.ScoreRangeJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.DashboardStat;
import com.cie.hr.infrastructure.service.viewmodel.DisputeOnlyVm;
import com.cie.hr.infrastructure.service.viewmodel.EmployeeListVm;
import com.cie.hr.infrastructure.service.viewmodel.EmployeePerformanceVm;
import com.cie.hr.infrastructure.service.viewmodel.EmployeeVm;
import com.cie.hr.infrastructure.service.viewmodel.EvaluationVm;
import com.cie.hr.infrastructure.service.viewmodel.JobForEmployeeVm;
import com.cie.hr.infrastructure.service.viewmodel.PoleVm;
import com.cie.hr.infrastructure.service.viewmodel.ProfileVm;
import com.cie.hr.infrastructure.service.viewmodel.StatsTemplate;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import com.cie.hr.infrastructure.valueobject.FormSpecialLine;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class EmployeeQuery {

    private final EmployeeJpaRepository employeeJpaRepository;

    private final ScorecardJpaRepository scorecardJpaRepository;

    private final CustomAuthenticationManager customAuthenticationManager;

    private final CampaignJpaRepository campaignJpaRepository;

    private final DelegationJpaRepository delegationJpaRepository;

    private final JobJpaRepository jobJpaRepository;

    private final ScoreRangeJpaRepository scoreRangeJpaRepository;

    private final DisputesJpaRepository disputesJpaRepository;

    private final DerogationJpaRepository derogationJpaRepository;
    

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public EmployeeQuery(EmployeeJpaRepository employeeJpaRepository,
                         ScorecardJpaRepository scorecardJpaRepository,
                         CustomAuthenticationManager customAuthenticationManager,
                         CampaignJpaRepository campaignJpaRepository,
                         DelegationJpaRepository delegationJpaRepository,
                         JobJpaRepository jobJpaRepository,
                         ScoreRangeJpaRepository scoreRangeJpaRepository,
                         DisputesJpaRepository disputesJpaRepository,
                         DerogationJpaRepository derogationJpaRepository) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.scorecardJpaRepository = scorecardJpaRepository;
        this.customAuthenticationManager = customAuthenticationManager;
        this.campaignJpaRepository = campaignJpaRepository;
        this.delegationJpaRepository = delegationJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
        this.scoreRangeJpaRepository = scoreRangeJpaRepository;
        this.disputesJpaRepository = disputesJpaRepository;
        this.derogationJpaRepository = derogationJpaRepository;
    }

    public Optional<EmployeeVm> employeeDetail(UUID id) {
        return this.employeeJpaRepository.findById(id).map(employee -> {
            var job = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(id);
            OrganizationEntity organization = job.map(JobEntity::getOrganization).orElse(null);

            if (organization != null) {
                organization = findChiefOrganization(organization);
            }
            return createEmployeeVm(employee, job.orElse(null), organization);
        });
    }

    private OrganizationEntity findChiefOrganization(OrganizationEntity organization) {
        if (!organization.getType().getCode().equals("1")) {
            var chiefJob = organization.getParent();
            while (chiefJob != null) {
                if (chiefJob.getType().getCode().equals("1")) {
                    return chiefJob;
                }
                chiefJob = chiefJob.getParent();
            }
        }
        return organization;
    }

    private EmployeeVm createEmployeeVm(EmployeeEntity employee, JobEntity jobEntity, OrganizationEntity organization) {
        return new EmployeeVm(
                employee.getId(),
                employee.getLastname(),
                employee.getFirstname(),
                employee.getEmployeeNumber(),
                employee.getEmail(),
                employee.getProfile().getName(),
                jobEntity == null ? null : jobEntity.getTitle(),
                jobEntity == null ? null : jobEntity.getGrade().getName(),
                new ProfileVm(employee.getProfile().getId(), employee.getProfile().getName()),
                employee.getAccessLevel() == null ? null : "Niveau d'accès" + employee.getAccessLevel(),
                jobEntity == null ? null : new JobForEmployeeVm(
                        jobEntity.getId(),
                        jobEntity.getTitle(),
                        new PoleVm(
                                organization == null ? null : organization.getId(),
                                organization == null ? null : organization.getName()
                        )
                ),
                !employee.getIsNotLocked()
        );
    }

    public List<EmployeeVm> allEmployees() {
        var employees = employeeJpaRepository.findAllEmployeeJobs();
        if (employees.isEmpty()) {
            return new ArrayList<>();
        } else {
            return employees.stream().map(this::employeeToVm).toList();
        }
    }

    public List<EmployeeVm> employeesWithoutJobs() {
        var employees = employeeJpaRepository.findAllEmployeeWithoutJobs();
        if (employees.isEmpty()) {
            return new ArrayList<>();
        } else {
            return employees.stream().map(this::employeeToVm).toList();
        }
    }

    private EmployeeVm employeeToVm(EmployeeEntity e) {
        OrganizationEntity organization = e.getJob() == null ? null : e.getJob().getOrganization();
        if (organization != null) {
            organization = findChiefOrganization(organization);
        }
        return createEmployeeVm(e, e.getJob(), organization);
    }

    public Optional<List<EmployeePerformanceVm>> getPerformanceScorecards(String userType) {
        var currentUser = customAuthenticationManager.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new InfrastructureException("Cet utilisateur n'existe pas");
        } else {
            var findEmployee = employeeJpaRepository.findByEmailAndDeletedFalse(currentUser);
            if (findEmployee.isPresent()) {
                List<EmployeePerformanceVm> employeeListVms = new ArrayList<>();
                EmployeeEntity currentEmployee = findEmployee.get();

                CampaignEntity lastClosedCampaign = findLastClosedCampaign();
                if (lastClosedCampaign != null) {
                    LOGGER.info("La dernière campagne fermée est: {}", lastClosedCampaign.getName());
                    List<DerogationEntity> derogationList = derogationJpaRepository.findByEmployeeIdAndCampaignId(currentEmployee.getId(), lastClosedCampaign.getId());
                    derogationList.forEach(e -> {
                        Optional<ScorecardEntity> scorecard = getScorecard(e.getCampaign().getId(), currentEmployee.getId());
                        LOGGER.info("Scorecard: {}", scorecard);
                        scorecard.ifPresent(value -> employeeListVms.add(getEmployeePerformanceVm(value)));
                    });
                } else {
                    LOGGER.info("Aucune campagne n'est fermée");
                }

                var findCampaign = campaignJpaRepository.findFirstByStatusCode("1");
                if (findCampaign.isPresent()) {
                    CampaignEntity campaign = findCampaign.get();
                    // Check profile
                    boolean isRh = false;
                    if (currentEmployee.getProfile().getCode().equals("RHU")) {
                        if (userType.equals("RH")) {
                            isRh = true;
                        }
                    } else if (currentEmployee.getProfile().getCode().equals("RH")) {
                        isRh = true;
                    }
                    LOGGER.info("Le code de l'employé est : {}", currentEmployee.getProfile().getCode());
                    if (isRh) {
                        Integer accessLevel = currentEmployee.getAccessLevel();
                        int level = Objects.requireNonNullElse(accessLevel, 0);
                        try {
                            List<String> codes;
                            switch (level) {
                                case 1 -> {
                                    codes = List.of("SD", "DR");
                                    var findScorecards = scorecardJpaRepository.findAllScorecardWithCampaignAndGrade(codes, campaign.getId());
                                    if (!findScorecards.isEmpty()) {
                                        findScorecards.forEach(scorecard -> employeeListVms.add(getEmployeePerformanceVm(scorecard)));
                                        return Optional.of(employeeListVms);
                                    }
                                }
                                case 2 -> {
                                    codes = List.of("D", "DA", "SD", "DR");
                                    var findScorecards = scorecardJpaRepository.findAllScorecardWithCampaignAndGrade(codes, campaign.getId());
                                    findScorecards.forEach(scorecard -> employeeListVms.add(getEmployeePerformanceVm(scorecard)));
                                    return Optional.of(employeeListVms);
                                }
                                case 3 -> {
                                    codes = List.of("DC", "D", "DA", "SD", "DR");
                                    var findScorecards = scorecardJpaRepository.findAllScorecardWithCampaignAndGrade(codes, campaign.getId());
                                    if (!findScorecards.isEmpty()) {
                                        findScorecards.forEach(scorecard -> employeeListVms.add(getEmployeePerformanceVm(scorecard)));
                                        return Optional.of(employeeListVms);
                                    }
                                }
                                case 4 -> {
                                    var findScorecards = scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaign.getId());
                                    LOGGER.info("Scorecards: {}", findScorecards.size());
                                    findScorecards.forEach(scorecard -> employeeListVms.add(getEmployeePerformanceVm(scorecard)));
                                    return Optional.of(employeeListVms);
                                }
                                default ->
                                        throw new InfrastructureException("Vous n'êtes pas autorisé à effectué cette action");
                            }
                        } catch (InfrastructureException ex) {
                            throw new InfrastructureException("Vous n'êtes pas autorisé à effectué cette action");
                        }
                    } else {
                        var findScorecards = scorecardJpaRepository.findAllByDeletedFalseAndAssessedIdAndCampaignStatusNameNot(currentEmployee.getId(), "notStarted");
                        if (!findScorecards.isEmpty()) {
                            findScorecards.forEach(scorecard -> employeeListVms.add(getEmployeePerformanceVm(scorecard)));
                        }
                    }
                }
                return Optional.of(employeeListVms);
            }
            return Optional.empty();
        }
    }

    public Optional<List<EmployeePerformanceVm>> getLastYearPerformanceScorecards(String userType, UUID campaignId) {
        try {
            var currentUser = customAuthenticationManager.getCurrentUser();
            if (currentUser == null || currentUser.isEmpty()) {
                throw new InfrastructureException("Cet utilisateur n'existe pas");
            } else {
                var findEmployee = employeeJpaRepository.findByEmailAndDeletedFalse(currentUser);
                if (findEmployee.isPresent()) {
                    var findCampaign = campaignJpaRepository.findById(campaignId);
                    if (findCampaign.isPresent()) {
                        if (!findCampaign.get().getStatus().getCode().equals("2")) {
                            throw new InfrastructureException("La campagne n'est pas encore terminée");
                        }
                        List<EmployeePerformanceVm> employeeListVms;
                        // Check profile
                        EmployeeEntity currentEmployee = findEmployee.get();
                        boolean isRh = false;
                        if (currentEmployee.getProfile().getCode().equals("RHU")) {
                            if (userType.equals("RH")) {
                                isRh = true;
                            }
                        } else if (currentEmployee.getProfile().getCode().equals("RH")) {
                            isRh = true;
                        }
                        List<ScorecardEntity> findScorecards;
                        if (isRh) {
                            int accessLevel = currentEmployee.getAccessLevel() == null ? 0 : currentEmployee.getAccessLevel();
                            try {
                                List<String> codes = getStrings(accessLevel);
                                findScorecards = scorecardJpaRepository.findAllScorecardWithCampaignAndGrade(codes, findCampaign.get().getId());
                            } catch (InfrastructureException ex) {
                                throw new InfrastructureException("Vous n'êtes pas autorisé à effectué cette action");
                            }
                        } else {
                            findScorecards = scorecardJpaRepository.findAllByDeletedFalseAndAssessedIdAndCampaignStatusNameNot(currentEmployee.getId(), "notStarted");
                        }
                        employeeListVms = findScorecards.stream().map(this::getEmployeePerformanceVm).collect(Collectors.toList());
                        return Optional.of(employeeListVms);
                    }
                }
                return Optional.empty();
            }
        } catch (Exception e) {
            LOGGER.error("Error while getting last year performance scorecards", e);
            throw new ApplicationException("Erreur lors de la récupération des évaluations de l'année dernière");
        }
    }

    @NotNull
    private static List<String> getStrings(int level) {
        List<String> codes;
        switch (level) {
            case 1 -> codes = List.of("SD", "DR");
            case 2 -> codes = List.of("D", "DA", "SD", "DR");
            case 3 -> codes = List.of("DC", "D", "DA", "SD", "DR");
            case 4 -> codes = List.of("DGA", "CE", "DC", "D", "DA", "SD", "DR");
            default -> throw new InfrastructureException("Vous n'êtes pas autorisé à effectué cette action");
        }
        return codes;
    }

    private List<EmployeeListVm> getEmployeeListVms(UUID campaignId, UUID employeeId, UUID currentUserId) {
        List<JobEntity> jobEntities = getJobEntitiesList(campaignId, employeeId);
        LOGGER.info("Job entities size: {}", jobEntities.size());
        if (jobEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return jobEntities.stream().map(job -> {
            if (job.getEmployee() == null) {
                return null;
            }
            Optional<ScorecardEntity> scorecard = getScorecard(campaignId, job.getEmployee().getId());
            return scorecard.map(value -> getEmployee(value, job, currentUserId)).orElse(null);
        }).filter(Objects::nonNull).toList();
    }


    private List<EmployeeListVm> getDerogatedEmployeeList(UUID employeeId) {
        List<DerogationEntity> delegationList = derogationJpaRepository.findAllByMangerIdAndDeletedFalse(employeeId);

        return delegationList.stream().map(delegation -> {
            JobEntity job = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(delegation.getEmployee().getId()).orElse(null);
            if (job == null) {
                return null;
            }

            Optional<ScorecardEntity> scorecard = getScorecard(delegation.getCampaign().getId(), job.getEmployee().getId());
            return scorecard.map(value -> getEmployee(value, job, employeeId)).orElse(null);
        }).filter(Objects::nonNull).toList();
    }

    public List<EmployeeListVm> getEvaluationScorecards() {
        var currentUser = customAuthenticationManager.getCurrentUser();
        List<EmployeeListVm> employeeListVmList = new ArrayList<>();
        //Set<EmployeeListVm> employeeListVmSet = new HashSet<>();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new InfrastructureException("Cet utilisateur n'existe pas");
        } else {
            var findEmployee = employeeJpaRepository.findByEmailAndDeletedFalse(currentUser);
            if (findEmployee.isPresent()) {
                employeeListVmList.addAll(getDerogatedEmployeeList(findEmployee.get().getId()));
                LOGGER.info("Employee list size : {}", employeeListVmList);
                var findCampaign = campaignJpaRepository.findFirstByStatusCode("1");
                findCampaign.ifPresent(campaignEntity -> employeeListVmList.addAll(getEmployeeListVms(campaignEntity.getId(), findEmployee.get().getId(), findEmployee.get().getId())));
                LOGGER.info("Employee list : {}", employeeListVmList);
            }
            return employeeListVmList;
            //return new ArrayList<>(employeeListVmSet);
        }
    }

    public Object getEvaluationScorecardDetails(UUID scorecardId) {
        var scorecard = scorecardJpaRepository.findById(scorecardId).orElse(null);
        if (scorecard == null) {
            return null;
        }
        JobEntity findEmployeeJob = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(scorecard.getAssessed().getId()).orElse(null);
        if (findEmployeeJob == null) {
            return null;
        }
        
        // Retrieve job template for dynamic objective injection
        JobEntity job = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(scorecard.getAssessed().getId()).orElse(null);
        FormSpecialSection jobTemplate = (job != null) ? job.getJobTemplate() : null;
        
        if (findEmployeeJob.getGrade().getCode().equals("CE")) {
            // Expert scorecard - inject job template into Section B
            EvaluationScorecardExpert expertTemplate = scorecard.getExpertTemplate();
            if (expertTemplate != null && jobTemplate != null && expertTemplate.forms() != null) {
                // Merge saved sectionB with job template (preserve achieved, note values from saved data)
                FormSpecialSection savedSectionB = expertTemplate.forms().sectionB();
                List<FormSpecialLine> mergedLines = mergeSpecialLines(jobTemplate.lines(), savedSectionB != null ? savedSectionB.lines() : null);
                
                ScorecardForExpert updatedForms = new ScorecardForExpert(
                    expertTemplate.forms().sectionA(),
                    new FormSpecialSection(
                        jobTemplate.title(),
                        savedSectionB != null ? savedSectionB.note() : jobTemplate.note(),
                        jobTemplate.type(),
                        mergedLines,
                        jobTemplate.coefficient(),
                        savedSectionB != null ? savedSectionB.completed() : jobTemplate.completed()
                    ),
                    expertTemplate.forms().sectionC(),
                    expertTemplate.forms().sectionD()
                );
                // Return full EvaluationScorecardExpert with updated forms
                return new EvaluationScorecardExpert(
                    expertTemplate.campaignId(),
                    expertTemplate.note(),
                    expertTemplate.status(),
                    updatedForms
                );
            }
            return expertTemplate;
        } else {
            // Manager scorecard - inject job template into Section D
            EvaluationScorecardManager managerTemplate = scorecard.getManagerTemplate();
            if (managerTemplate != null && jobTemplate != null && managerTemplate.forms() != null) {
                // Merge saved sectionD with job template (preserve achieved, note values from saved data)
                FormSpecialSection savedSectionD = managerTemplate.forms().sectionD();
                List<FormSpecialLine> mergedLines = mergeSpecialLines(jobTemplate.lines(), savedSectionD != null ? savedSectionD.lines() : null);
                
                ScorecardForManagerForm updatedForms = new ScorecardForManagerForm(
                    managerTemplate.forms().sectionA(),
                    managerTemplate.forms().sectionB(),
                    managerTemplate.forms().sectionC(),
                    new FormSpecialSection(
                        jobTemplate.title(),
                        savedSectionD != null ? savedSectionD.note() : jobTemplate.note(),
                        jobTemplate.type(),
                        mergedLines,
                        jobTemplate.coefficient(),
                        savedSectionD != null ? savedSectionD.completed() : jobTemplate.completed()
                    ),
                    managerTemplate.forms().sectionE(),
                    managerTemplate.forms().sectionF()
                );
                // Return full EvaluationScorecardManager with updated forms
                return new EvaluationScorecardManager(
                    managerTemplate.campaignId(),
                    managerTemplate.note(),
                    managerTemplate.status(),
                    updatedForms
                );
            }
            return managerTemplate;
        }
    }
    
    /**
     * Merge job template lines with saved lines, preserving achieved and note values from saved data
     */
    private List<FormSpecialLine> mergeSpecialLines(List<FormSpecialLine> templateLines, List<FormSpecialLine> savedLines) {
        if (templateLines == null) {
            return savedLines;
        }
        if (savedLines == null || savedLines.isEmpty()) {
            return templateLines;
        }
        
        // Create a map of saved lines by title for quick lookup
        Map<String, FormSpecialLine> savedLinesMap = savedLines.stream()
            .collect(Collectors.toMap(FormSpecialLine::title, line -> line, (a, b) -> b));
        
        // Merge: use template structure but preserve achieved/note from saved data
        return templateLines.stream()
            .map(templateLine -> {
                FormSpecialLine savedLine = savedLinesMap.get(templateLine.title());
                if (savedLine != null) {
                    // Preserve achieved, note from saved data
                    return new FormSpecialLine(
                        templateLine.title(),
                        templateLine.coefficient(),
                        savedLine.note(),
                        templateLine.objective(),
                        savedLine.achieved(),
                        templateLine.unit(),
                        templateLine.noteId()
                    );
                }
                return templateLine;
            })
            .collect(Collectors.toList());
    }

    public List<EmployeeVm> retrieveAllEmployeeWithHighGrade(UUID employeeId) {
        var employee = employeeJpaRepository.findById(employeeId);
        if (employee.isPresent()) {
            JobEntity findEmployeeJob = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(employeeId).orElse(null);
            if (findEmployeeJob == null) {
                return new ArrayList<>();
            } else {
                var grade = findEmployeeJob.getGrade();
                return this.jobJpaRepository.findByJobGradeRankLessThan(grade.getRank()).stream().map(e -> {
                    OrganizationEntity organization = e.getOrganization();

                    if (organization != null) {
                        organization = findChiefOrganization(organization);
                    }
                    return createEmployeeVm(e.getEmployee(), e, organization);

                }).toList();
            }

        }
        return new ArrayList<>();
    }

    public List<EvaluationVm> lastYearEvaluationStatistics(UUID id) {
        var currentUser = customAuthenticationManager.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new InfrastructureException("Cet utilisateur n'existe pas");
        }
        int currentYear = LocalDate.now().getYear();
        Optional<EmployeeEntity> findEmployee = employeeJpaRepository.findById(id);
        if (findEmployee.isPresent()) {
            List<EvaluationVm> evaluationVmList = new ArrayList<>();
            var lastScorecards = scorecardJpaRepository.findFirst3ByAssessedIdOrderByCampaignEndDateDesc(id);
            if (!lastScorecards.isEmpty()) {
                lastScorecards.forEach(scorecard -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(scorecard.getCampaign().getEndDate());

                    double note = (scorecard.getManagerTemplate() != null) ? scorecard.getManagerTemplate().note() : scorecard.getExpertTemplate().note();

                    Optional<ScoreRangeEntity> scoreRange = scoreRangeJpaRepository.findFirstByMinRangeLessThanEqualAndMaxRangeGreaterThanEqual(note, note);
                    String label = scoreRange.map(ScoreRangeEntity::getLabel).orElse("");

                    evaluationVmList.add(new EvaluationVm(label, note, cal.get(Calendar.YEAR)));

                });
            } else {
                evaluationVmList.add(new EvaluationVm("", 0d, currentYear));
                evaluationVmList.add(new EvaluationVm("", 0d, currentYear - 1));
                evaluationVmList.add(new EvaluationVm("", 0d, currentYear - 2));
            }
            return evaluationVmList;
        } else {
            return new ArrayList<>() {{
                add(new EvaluationVm("", 0d, currentYear));
                add(new EvaluationVm("", 0d, currentYear - 1));
                add(new EvaluationVm("", 0d, currentYear - 2));
            }};
        }
    }

    public Optional<List<EmployeeListVm>> collaboratorsEvaluationScorecards(UUID campaignId) {
        var currentUser = customAuthenticationManager.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new InfrastructureException("Cet utilisateur n'existe pas");
        } else {
            var findEmployee = employeeJpaRepository.findByEmailAndDeletedFalse(currentUser);
            if (findEmployee.isPresent()) {
                var findCampaign = campaignJpaRepository.findById(campaignId);
                if (findCampaign.isPresent()) {

                    List<DelegationEntity> delegationToRemoveList = delegationJpaRepository.findByCampaignIdAndGiverIdAndDeletedFalse(findCampaign.get().getId(), findEmployee.get().getId());
                    List<UUID> toRemoveList = delegationToRemoveList.stream().map(e -> e.getEmployee().getId()).toList();
                    Optional<JobEntity> employeeJob = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(findEmployee.get().getId());
                    if (employeeJob.isPresent()) {
                        List<EmployeeListVm> employeeListVms;
                        if (employeeJob.get().getParent() == null) {
                            List<ScorecardEntity> employeeScorecardList = scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaignId);
                            employeeListVms = employeeScorecardList.stream().map(e -> new EmployeeListVm(
                                    e.getId(),
                                    e.getAssessed().getLastname(),
                                    e.getAssessed().getFirstname(),
                                    e.getAssessed().getEmployeeNumber(),
                                    e.getAssessed().getId(),
                                    e.getAssessed().getEmail(),
                                    e.getJob() == null ? null : e.getJob().getTitle(),
                                    e.getStatus().getId(),
                                    e.getStatus().getName(),
                                    e.getExpertTemplate() == null,
                                    e.getEvaluated_at(),
                                    e.getJob() == null ? null : e.getJob().getOrganization(),
                                    e.getJob() == null ? null : e.getJob().getOrganization_type(),
                                    e.getJob() == null ? null : e.getJob().getGrade(),
                                    toRemoveList.contains(e.getAssessed().getId()),
                                    disputesJpaRepository.findFirstByScorecardId(e.getId()).stream().map(DisputesMapper::toDisputeOnlyVm).collect(Collectors.toList()),
                                    toRemoveList.contains(e.getAssessed().getId()) ? findEmployee.get().getId() : null,
                                    e.getExpertTemplate() == null ? e.getManagerTemplate().note() : e.getExpertTemplate().note(),
                                    e.getCampaign().getName()
                            )).collect(Collectors.toList());
                        } else {
                            OrganizationEntity organizationEntity = employeeJob.get().getOrganization();
                            if (organizationEntity != null) {
                                List<ScorecardEntity> scorecardEntities;
                                List<JobEntity> jobEntities;
                                List<UUID> employeesIds;

                                jobEntities = jobJpaRepository.findByDeletedFalseAndParentIdAndEmployeeIdIsNotNullAndEmployeeIdNot(employeeJob.get().getId(), employeeJob.get().getEmployee().getId());
                                employeesIds = jobEntities.stream()
                                        .flatMap(jobEntity -> Stream.concat(
                                                Stream.of(jobEntity.getEmployee().getId()),
                                                jobJpaRepository.findByDeletedFalseAndParentIdAndEmployeeIdIsNotNullAndEmployeeIdNot(jobEntity.getEmployee().getId(), jobEntity.getEmployee().getId()).stream()
                                                        .map(e -> e.getEmployee() == null ? null : e.getEmployee().getId())
                                        ))
                                        .collect(Collectors.toList());

                                scorecardEntities = scorecardJpaRepository.findByDeletedFalseAndAssessedIdInAndCampaignId(employeesIds, campaignId);
                                employeeListVms = scorecardEntities.stream().map(e -> new EmployeeListVm(
                                        e.getId(),
                                        e.getAssessed().getLastname(),
                                        e.getAssessed().getFirstname(),
                                        e.getAssessed().getEmployeeNumber(),
                                        e.getAssessed().getId(),
                                        e.getAssessed().getEmail(),
                                        e.getJob() == null ? null : e.getJob().getTitle(),
                                        e.getStatus().getId(),
                                        e.getStatus().getName(),
                                        e.getExpertTemplate() == null,
                                        e.getEvaluated_at(),
                                        e.getJob() == null ? null : e.getJob().getOrganization_type(),
                                        e.getJob() == null ? null : e.getJob().getOrganization(),
                                        e.getJob() == null ? null : e.getJob().getGrade(),
                                        toRemoveList.contains(e.getAssessed().getId()),
                                        disputesJpaRepository.findFirstByScorecardId(e.getId()).stream().map(DisputesMapper::toDisputeOnlyVm).collect(Collectors.toList()),
                                        toRemoveList.contains(e.getAssessed().getId()) ? delegationToRemoveList.stream().filter(x -> x.getEmployee().getId().equals(e.getAssessed().getId())).findFirst().map(elt -> elt.getGiver().getId()).orElse(null) : null,
                                        e.getExpertTemplate() == null ? e.getManagerTemplate().note() : e.getExpertTemplate().note(),
                                        e.getCampaign().getName()
                                )).collect(Collectors.toList());
                            } else {
                                employeeListVms = new ArrayList<>();
                            }
                        }
                        return Optional.of(employeeListVms);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public DashboardStat dashboardStat(UUID campaignId, String userType) {
        if (userType.equals("RH") || userType.equals("RHU")) {
            LOGGER.info("Je suis dans le bloc RH");
            var collaborators = getPerformanceScorecards(userType);
            LOGGER.info("Collaborators: {}", collaborators);
            StatsTemplate my_teams_stats;
            if (collaborators.isEmpty()) {
                my_teams_stats = new StatsTemplate(0, 0);
            } else {
                var collaboratorsList = collaborators.get();
                var collaboratorsCount = collaboratorsList.size();
                var collaboratorsWithEvaluationCount = collaboratorsList.stream().filter(e -> e.evaluated_at() != null).count();
                my_teams_stats = new StatsTemplate(collaboratorsCount, (int) collaboratorsWithEvaluationCount);
            }
            return new DashboardStat(
                    my_teams_stats,
                    new StatsTemplate(0, 0)
            );
        }
        var collaborators = collaboratorsEvaluationScorecards(campaignId);
        var myScorecards = getEvaluationScorecards();
        StatsTemplate my_teams_stats;
        if (collaborators.isEmpty()) {
            my_teams_stats = new StatsTemplate(0, 0);
        } else {
            var collaboratorsList = collaborators.get();
            var collaboratorsCount = collaboratorsList.size();
            var collaboratorsWithEvaluationCount = collaboratorsList.stream().filter(e -> e.evaluated_at() != null).count();
            my_teams_stats = new StatsTemplate(collaboratorsCount, (int) collaboratorsWithEvaluationCount);
        }

        StatsTemplate my_stats;
        if (myScorecards.isEmpty()) {
            my_stats = new StatsTemplate(0, 0);
        } else {
            var myScorecardsWithEvaluationCount = myScorecards.stream().filter(e -> e.evaluated_at() != null).count();
            my_stats = new StatsTemplate(myScorecards.size(), (int) myScorecardsWithEvaluationCount);
        }

        return new DashboardStat(
                my_stats,
                my_teams_stats
        );
    }

    private boolean checkIfLastClosedCampaign(UUID campaignId) {
        if (campaignId == null) {
            throw new IllegalArgumentException("Campaign ID must not be null");
        }
        CampaignEntity campaign = findLastClosedCampaign();
        return campaign != null && campaign.getId().equals(campaignId);
    }

    private CampaignEntity findLastClosedCampaign() {
        return campaignJpaRepository.findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc("2").orElse(null);
    }

    private boolean checkIfDerogationExist(UUID campaignId, UUID employeeId) {
        if (campaignId == null || employeeId == null) {
            throw new IllegalArgumentException("Campaign ID or Employee must not be null");
        }
        List<DerogationEntity> derogation = derogationJpaRepository.findByEmployeeIdAndCampaignId(employeeId, campaignId);
        return !derogation.isEmpty() && derogation.stream().anyMatch(e -> !e.isDeleted());
    }

    private boolean checkIfEmployeeCanBeEvaluated(UUID campaignId, UUID employeeId) {
        if (campaignId == null || employeeId == null) {
            throw new IllegalArgumentException("Campaign ID or Employee must not be null");
        }
        return checkIfLastClosedCampaign(campaignId) && checkIfDerogationExist(campaignId, employeeId);
    }

    private boolean checkIfScorecardCanBeDerogate(UUID campaignId, UUID employeeId) {
        if (campaignId == null || employeeId == null) {
            throw new IllegalArgumentException("Campaign ID or Employee must not be null");
        }
        return checkIfLastClosedCampaign(campaignId) && !checkIfDerogationExist(campaignId, employeeId);
    }

    private EmployeePerformanceVm getEmployeePerformanceVm(ScorecardEntity scorecard) {
        JobEntity findEmployeeJob = jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(scorecard.getAssessed().getId()).orElse(null);
        return new EmployeePerformanceVm(
                scorecard.getId(),
                scorecard.getCampaign().getName(),
                scorecard.getManager() == null ? null :
                        scorecard.getManager().getFirstname(),
                scorecard.getAssessed().getLastname(),
                scorecard.getAssessed().getFirstname(),
                scorecard.getAssessed().getEmployeeNumber(),
                scorecard.getAssessed().getId(),
                scorecard.getAssessed().getEmail(),
                scorecard.getJob() == null ? findEmployeeJob == null ? null :
                        findEmployeeJob.getTitle() :
                        scorecard.getJob().getTitle(),
                scorecard.getStatus().getId(),
                scorecard.getStatus().getName(),
                scorecard.getExpertTemplate() == null ? scorecard.getManagerTemplate().note() :
                        scorecard.getExpertTemplate().note(),
                scorecard.getEvaluated_at(),
                scorecard.getCampaign().getStartDate(),
                scorecard.getCampaign().getEndDate(),
                scorecard.getJob() == null ?
                        findEmployeeJob == null ? null :
                                findEmployeeJob.getOrganization().getType().getName() :
                        scorecard.getJob().getOrganization_type(),
                scorecard.getJob() == null ?
                        findEmployeeJob == null ? null :
                                findEmployeeJob.getOrganization().getName() :
                        scorecard.getJob().getOrganization(),
                scorecard.getJob() == null ? findEmployeeJob == null ? null :
                        findEmployeeJob.getGrade().getName() :
                        scorecard.getJob().getGrade(),
                checkIfEmployeeCanBeEvaluated(scorecard.getCampaign().getId(), scorecard.getAssessed().getId()),
                checkIfScorecardCanBeDerogate(scorecard.getCampaign().getId(), scorecard.getAssessed().getId())
        );
    }

    private List<DisputeOnlyVm> getDisputes(UUID scorecardId) {
        return disputesJpaRepository.findFirstByScorecardId(scorecardId).stream().map(DisputesMapper::toDisputeOnlyVm).toList();
    }

    private UUID getDelegateBy(UUID campaignId, UUID employeeId, UUID currentUser) {
        List<UUID> toRemoveList = getUUIDToRemoveList(campaignId, currentUser);
        List<DelegationEntity> delegationEntityList = getDelegationList(campaignId, currentUser);
        List<DelegationEntity> delegationGiveByCurrentUser = getDelegationGiveByCurrentUser(campaignId, currentUser);
        LOGGER.info("Delegate by Remove list: {}", toRemoveList);
        return toRemoveList.contains(employeeId) ?
                delegationEntityList.stream()
                        .filter(x -> x.getEmployee().getId().equals(employeeId))
                        .findFirst()
                        .map(elt -> elt.getGiver().getId()).orElse(null) :
                delegationGiveByCurrentUser.stream()
                        .filter(x -> x.getEmployee().getId().equals(employeeId))
                        .findFirst()
                        .map(elt -> elt.getGiver().getId()).orElse(null);
    }

    private List<DelegationEntity> getDelegationList(UUID campaignId, UUID employeeId) {
        return delegationJpaRepository.findByCampaignIdAndReceiverIdAndDeletedFalse(campaignId, employeeId);
    }

    private List<DelegationEntity> getDelegationGiveByCurrentUser(UUID campaignId, UUID employeeId) {
        return delegationJpaRepository.findByCampaignIdAndGiverIdAndDeletedFalse(campaignId, employeeId);
    }

    private List<UUID> getUUIDToRemoveList(UUID campaignId, UUID employeeId) {
        return getDelegationList(campaignId, employeeId).stream().map(e -> e.getEmployee().getId()).toList();
    }

    private List<UUID> currentEmployeeDelegationIds(UUID campaignId, UUID employeeId) {
        return getDelegationGiveByCurrentUser(campaignId, employeeId).stream().map(e -> e.getEmployee().getId()).toList();
    }

    private List<JobEntity> getJobEntitiesList(UUID campaignId, UUID employeeId) {
        var delegationList = getDelegationList(campaignId, employeeId);
        JobEntity job = findEmployeeJob(employeeId);
        if (job == null) {
            return new ArrayList<>();
        }
        if (!delegationList.isEmpty()) {
            List<UUID> toRemoveList = getUUIDToRemoveList(campaignId, employeeId);
            LOGGER.info("To remove list: {}", toRemoveList);
            return jobJpaRepository.findByParentIdOrEmployeeIdIn(job.getId(), toRemoveList);
        } else {
            LOGGER.info("Delegation list is empty");
            return jobJpaRepository.findByParentId(job.getId());
        }
    }

    private JobEntity findEmployeeJob(UUID employeeId) {
        return jobJpaRepository.findFirstByEmployeeIdAndDeletedFalseOrderByCreatedDesc(employeeId).orElse(null);
    }

    private Optional<ScorecardEntity> getScorecard(UUID campaignId, UUID employeeId) {
        return scorecardJpaRepository.findFirstByDeletedFalseAndAssessedIdAndCampaignId(employeeId, campaignId);
    }

    private boolean isDelegate(UUID campaignId, UUID employeeId, UUID currentUser) {
        List<UUID> toRemoveList = getUUIDToRemoveList(campaignId, currentUser);
        List<UUID> myDelegationIds = currentEmployeeDelegationIds(campaignId, currentUser);
        LOGGER.info("Remove list: {}", toRemoveList);
        LOGGER.info("My delegation list: {}", myDelegationIds);
        LOGGER.info("My Employee Id {}", employeeId);
        return toRemoveList.contains(employeeId) || myDelegationIds.contains(employeeId);
    }

    private EmployeeListVm getEmployee(ScorecardEntity scorecard, JobEntity job, UUID currentUser) {
        EmployeeEntity employee = job.getEmployee();
        return new EmployeeListVm(
                scorecard.getId(),
                employee.getLastname(),
                employee.getFirstname(),
                employee.getEmployeeNumber(),
                employee.getId(),
                employee.getEmail(),
                scorecard.getJob() == null ? job.getTitle() : scorecard.getJob().getTitle(),
                scorecard.getStatus().getId(),
                scorecard.getStatus().getName(),
                scorecard.getExpertTemplate() == null,
                scorecard.getEvaluated_at(),
                scorecard.getJob() == null ? job.getOrganization().getName() : scorecard.getJob().getOrganization(),
                scorecard.getJob() == null ? job.getOrganization().getType().getName() : scorecard.getJob().getOrganization_type(),
                scorecard.getJob() == null ? job.getGrade().getName() : scorecard.getJob().getGrade(),
                isDelegate(scorecard.getCampaign().getId(), employee.getId(), currentUser),
                getDisputes(scorecard.getId()),
                getDelegateBy(scorecard.getCampaign().getId(), employee.getId(), currentUser),
                scorecard.getExpertTemplate() == null ? scorecard.getManagerTemplate().note() : scorecard.getExpertTemplate().note(),
                scorecard.getCampaign().getName()
        );
    }
}

