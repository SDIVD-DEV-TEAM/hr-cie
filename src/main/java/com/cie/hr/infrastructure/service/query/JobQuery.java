package com.cie.hr.infrastructure.service.query;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.InfrastructureException;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.common.utils.CheckRHEmployee;
import com.cie.hr.infrastructure.entity.GradeEntity;
import com.cie.hr.infrastructure.entity.JobEntity;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.mapper.JobMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.JobVm;
import com.cie.hr.infrastructure.service.viewmodel.JobWithEmployeeInfoVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardExpertTemplateVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardManagerTemplateVm;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class JobQuery {

    private final JobJpaRepository jobJpaRepository;
    private final GradeJpaRepository gradeJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final CustomAuthenticationManager customAuthenticationManager;
    private final EmployeeJpaRepository employeeJpaRepository;
    private final CheckRHEmployee checkRHEmployee;
    private final ScorecardTemplateQuery scorecardTemplateQuery;
    private static final String EXPERT_GRADE = "CE";
    private static final String MANAGER_GRADE = "CM";

    public JobQuery(JobJpaRepository jobJpaRepository, GradeJpaRepository gradeJpaRepository, OrganizationJpaRepository organizationJpaRepository, CustomAuthenticationManager customAuthenticationManager, EmployeeJpaRepository employeeJpaRepository, CheckRHEmployee checkRHEmployee, ScorecardTemplateQuery scorecardTemplateQuery) {
        this.jobJpaRepository = jobJpaRepository;
        this.gradeJpaRepository = gradeJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
        this.customAuthenticationManager = customAuthenticationManager;
        this.employeeJpaRepository = employeeJpaRepository;
        this.checkRHEmployee = checkRHEmployee;
        this.scorecardTemplateQuery = scorecardTemplateQuery;
    }

    public List<JobVm> readAllJobs() {
        return jobJpaRepository.findByDeletedFalseOrderByCreatedDesc().stream().map(JobMapper::toJobVm).toList();
    }

    public List<JobWithEmployeeInfoVm> readAllOccupiedJobs() {
        return jobJpaRepository.findAllByDeletedFalseAndEmployeeIdIsNotNull().stream().map(JobMapper::toJobWithEmployeeInfo).toList();
    }

    public List<JobWithEmployeeInfoVm> readAllAvailableJobs() {
        return jobJpaRepository.findAllByDeletedFalseAndEmployeeIdIsNull().stream().map(JobMapper::toJobWithEmployeeInfo).toList();
    }

    public List<JobWithEmployeeInfoVm> readAllAvailableJobsInOrganization(UUID id) {
        return jobJpaRepository.findAllByDeletedFalseAndEmployeeIdIsNullAndOrganizationId(id).stream().map(JobMapper::toJobWithEmployeeInfo).toList();
    }

    public List<JobWithEmployeeInfoVm> readAllJobsInOrganization(UUID id) {
        return jobJpaRepository.findAllByDeletedFalseAndOrganizationId(id).stream().map(JobMapper::toJobWithEmployeeInfo).toList();
    }

    public JobVm viewDetails(UUID jobId) {
        var job = jobJpaRepository.findById(jobId);
        if (job.isEmpty()) {
            return null;
        }
        return job.map(JobMapper::toJobVm).get();
    }

    public List<JobWithEmployeeInfoVm> retrieveAllEmployeesUnderManagementLine(UUID jobParentId) {
        return jobJpaRepository.findByParentId(jobParentId).stream().map(JobMapper::toJobWithEmployeeInfo).toList();
    }

    public List<JobWithEmployeeInfoVm> retrieveAllJobsForSpecificGrade(UUID organizationId, UUID gradeId) {
        Optional<OrganizationEntity> checkOrganization = organizationJpaRepository.findById(organizationId);
        if (checkOrganization.isPresent()) {
            OrganizationEntity currentOrganization = checkOrganization.get().getParent();
            if (currentOrganization != null) {
                JobEntity managerJob = currentOrganization.getChiefJob();
                if (managerJob != null) {
                    Optional<GradeEntity> grade = gradeJpaRepository.findById(gradeId);
                    if (grade.isPresent() && managerJob.getGrade().getRank() < grade.get().getRank()) {
                        return new ArrayList<>() {{
                            add(new JobWithEmployeeInfoVm(
                                    managerJob.getEmployee() == null ? null : managerJob.getEmployee().getId(),
                                    managerJob.getEmployee() == null ? null : managerJob.getEmployee().getFirstname(),
                                    managerJob.getEmployee() == null ? null : managerJob.getEmployee().getLastname(),
                                    managerJob.getOrganization() == null ? null : managerJob.getOrganization().getId(),
                                    managerJob.getOrganization() == null ? null : managerJob.getOrganization().getCode(),
                                    managerJob.getOrganization() == null ? null : managerJob.getOrganization().getName(),
                                    managerJob.getId(),
                                    managerJob.getTitle(),
                                    managerJob.getCode(),
                                    managerJob.getGrade() == null ? null : managerJob.getGrade().getName(),
                                    managerJob.getJobTemplate()
                            ));
                        }};
                    }
                }
            }
        }
        return null;
    }

    public List<JobWithEmployeeInfoVm> employeeFromPoleWithoutJobs(UUID poleId) {
        var currentUser = customAuthenticationManager.getCurrentUser();
        if (currentUser == null || currentUser.isEmpty()) {
            throw new InfrastructureException("Cet utilisateur n'existe pas");
        } else {
            var findEmployee = employeeJpaRepository.findByEmailAndDeletedFalse(currentUser);
            if (findEmployee.isEmpty()) {
                throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
            } else {
                if (checkRHEmployee.employeeIsNotRH()) {
                    throw new ApplicationException("Vous n'avez pas le droit pour effectuer cette action");
                }
            }
        }

        var findPole = organizationJpaRepository.findById(poleId);
        if (findPole.isEmpty()) {
            return new ArrayList<>();
        } else {
            List<OrganizationEntity> organizations = organizationJpaRepository.findChildrenAndGrandChildren(poleId);
            List<UUID> organisationIds = organizations.stream().map(OrganizationEntity::getId).toList();
            List<JobEntity> jobs = jobJpaRepository.findByOrganizationIdInAndEmployeeIdIsNull(organisationIds);
            return jobs.stream().map(JobMapper::toJobWithEmployeeInfo).toList();
        }
    }

    public Object getJobScorecard(UUID jobId) {
        JobEntity jobEntity = jobJpaRepository.findById(jobId).orElseThrow(() -> new ApplicationException("Job not found"));
        String gradeCode = jobEntity.getGrade().getCode();
        FormSpecialSection formSpecialSection = jobEntity.getJobTemplate();

        return createScorecard(gradeCode, formSpecialSection);
    }

    private Object createScorecard(String gradeCode, FormSpecialSection jobSpecialSection) {
        if (EXPERT_GRADE.equals(gradeCode)) {
            return buildScorecardForExpert(jobSpecialSection);
        } else {
            return buildScorecardForManager(jobSpecialSection);
        }
    }

    private ScorecardExpertTemplateVm buildScorecardForExpert(FormSpecialSection jobSpecialSection) {
        ScorecardExpertTemplateVm template = (ScorecardExpertTemplateVm) scorecardTemplateQuery.getScorecardTemplateByType(EXPERT_GRADE);
        ScorecardForExpert generatedForm = new ScorecardForExpert(
                template.form().sectionA(),
                jobSpecialSection != null ? jobSpecialSection : template.form().sectionB(),
                template.form().sectionC(),
                template.form().sectionD()
        );

        return new ScorecardExpertTemplateVm(template.id(), template.title(), template.type(), generatedForm);
    }

    private ScorecardManagerTemplateVm buildScorecardForManager(FormSpecialSection jobSpecialSection) {
        ScorecardManagerTemplateVm template = (ScorecardManagerTemplateVm) scorecardTemplateQuery.getScorecardTemplateByType(MANAGER_GRADE);
        ScorecardForManagerForm generatedForm = new ScorecardForManagerForm(
                template.form().sectionA(),
                template.form().sectionB(),
                template.form().sectionC(),
                jobSpecialSection != null ? jobSpecialSection : template.form().sectionD(),
                template.form().sectionE(),
                template.form().sectionF()
        );

        return new ScorecardManagerTemplateVm(template.id(), template.title(), template.type(), generatedForm);
    }
}
