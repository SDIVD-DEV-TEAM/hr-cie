package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.valueobject.JobEmbedded;
import com.cie.hr.infrastructure.entity.*;
import com.cie.hr.infrastructure.service.viewmodel.*;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public class JobMapper {

    public static Job toJobDomain(JobEntity jobEntity) {
        if (jobEntity == null) {
            return null;
        }
        var jobParent = jobEntity.getParent();
        var employee = jobEntity.getEmployee();
        return Job.newBuilder()
                .id(jobEntity.getId())
                .title(jobEntity.getTitle())
                .code(jobEntity.getCode())
                .grade(GradeMapper.toGradeDomain(jobEntity.getGrade()))
                .organizationId(OrganizationMapper.toOrganization(jobEntity.getOrganization()))
                .parentId(jobParent == null ? null : jobParent.getId())
                .employeeId(EmployeeMapper.toEmployeeDomain(employee))
                .isDeleted(jobEntity.isDeleted())
                .jobTemplate(jobEntity.getJobTemplate())
                .build();
    }

    public static JobEntity toJobEntity(Job job) {
        if (job == null) {
            return null;
        }
        var parentJobEntity = job.getParentId() == null ? null : JobEntity.builder().build();
        if (parentJobEntity != null) {
            parentJobEntity.setId(job.getParentId());
        }
        var jobEntity = JobEntity.builder()
                .title(job.getTitle())
                .grade(GradeMapper.toGradeEntity(job.getGrade()))
                .parent(parentJobEntity)
                .code(job.getCode())
                .employee(EmployeeMapper.toEmployeeEntity(job.getEmployeeId()))
                .organization(OrganizationMapper.toOrganizationEntity(job.getOrganizationId()))
                .jobTemplate(job.getJobTemplate())
                .build();
        jobEntity.setId(job.getId());
        jobEntity.setDeleted(jobEntity.isDeleted());
        return jobEntity;
    }

    public static JobVm toJobVm(JobEntity jobEntity) {
        if (jobEntity == null) {
            return null;
        }

        return new JobVm(
                jobEntity.getId(),
                jobEntity.getTitle(),
                jobEntity.getCode(),
                jobEntity.getJobTemplate(),
                jobEntity.getOrganization() == null ? null : jobEntity.getOrganization().getCode(),
                toGradeVm(jobEntity),
                toLightEmployeeVm(jobEntity.getParent() == null ? null : jobEntity.getParent().getEmployee()),
                toLightEmployeeVm(jobEntity.getEmployee()),
                OrganizationMapper.toOrganizationLightVm(jobEntity.getOrganization()),
                OrganizationTypeMapper.toOrganizationVm(
                        jobEntity.getOrganization() == null ? null :
                                jobEntity.getOrganization().getType()
                ),
                OrganizationTypeMapper.toOrganizationVm(
                        jobEntity.getOrganization() == null ? null :
                                jobEntity.getOrganization().getParent() == null ? null :
                                        jobEntity.getOrganization().getParent().getType()
                )
        );
    }

    public static JobWithEmployeeInfoVm toJobWithEmployeeInfo(JobEntity jobEntity) {
        if (jobEntity == null) {
            return null;
        }
        var employee = jobEntity.getEmployee();
        var organization = jobEntity.getOrganization();

        if (employee == null) {
            return new JobWithEmployeeInfoVm(
                    null,
                    "AVAILABLE",
                    "AVAILABLE",
                    organization.getId(),
                    organization.getCode(),
                    organization.getName(),
                    jobEntity.getId(),
                    jobEntity.getTitle(),
                    jobEntity.getCode(),
                    jobEntity.getGrade() == null ? null : jobEntity.getGrade().getName(),
                    jobEntity.getJobTemplate()
            );
        } else {
            return new JobWithEmployeeInfoVm(
                    employee.getId(),
                    employee.getFirstname(),
                    employee.getLastname(),
                    organization.getId(),
                    organization.getCode(),
                    organization.getName(),
                    jobEntity.getId(),
                    jobEntity.getTitle(),
                    jobEntity.getCode(),
                    jobEntity.getGrade() == null ? null : jobEntity.getGrade().getName(),
                    jobEntity.getJobTemplate()
            );
        }
    }

    public static JobEmbedded toJobEmbedded(JobEmbeddedEntity jobEntity) {
        if (jobEntity == null) {
            return null;
        }

        return new JobEmbedded(
                jobEntity.getTitle(),
                jobEntity.getCode(),
                jobEntity.getOrganization(),
                jobEntity.getGrade(),
                jobEntity.getOrganization_type()
        );
    }

    public static JobEmbeddedEntity toJobEmbeddedEntity(JobEmbedded job) {
        if (job == null) {
            return null;
        }

        return new JobEmbeddedEntity(
                job.title(),
                job.code(),
                job.organization(),
                job.grade(),
                job.organization_type()
        );
    }

    public static void updateAndSave(Job domain, JobEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getTitle(), entity.getTitle())) {
            entity.setTitle(domain.getTitle());
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        if (!Objects.equals(domain.getJobTemplate(), entity.getJobTemplate())) {
            entity.setJobTemplate(domain.getJobTemplate());
        }

        if (domain.getGrade() != null && (entity.getGrade() == null || !domain.getGrade().getId().equals(entity.getGrade().getId()))) {
            entity.setGrade(GradeMapper.toGradeEntity(domain.getGrade()));
        }

        OrganizationEntity organizationEntity = OrganizationMapper.toOrganizationEntity(domain.getOrganizationId());
        if (organizationEntity != null && (entity.getOrganization() == null ||
                !Objects.equals(entity.getOrganization(), organizationEntity))) {
            entity.setOrganization(organizationEntity);
        }

        if (domain.getParentId() == null) {
            entity.setParent(null);
        } else {
            if (entity.getParent() == null || !domain.getParentId().equals(entity.getParent().getId())) {
                var parentJobEntity = JobEntity.builder().build();
                parentJobEntity.setId(domain.getParentId());
                entity.setParent(parentJobEntity);
            }
        }

        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(domain.getEmployeeId());
        if (domain.getEmployeeId() == null) {
            entity.setEmployee(null);
        } else {
            if (employeeEntity != null && (entity.getEmployee() == null || !Objects.equals(entity.getEmployee(), employeeEntity))) {
                entity.setEmployee(employeeEntity);
            }
        }

        if (!Objects.equals(domain.isDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.isDeleted());
        }
    }

    private static LightEmployeeVm toLightEmployeeVm(EmployeeEntity employee) {
        if (employee == null) {
            return null;
        }
        return new LightEmployeeVm(
                employee.getId(),
                employee.getFirstname(),
                employee.getLastname(),
                null
        );
    }

    private static GradeVm toGradeVm(JobEntity jobEntity) {
        if (jobEntity.getGrade() == null) {
            return null;
        }
        return new GradeVm(
                jobEntity.getGrade().getId(),
                jobEntity.getGrade().getName(),
                jobEntity.getGrade().getDescription(),
                jobEntity.getGrade().getCode()
        );
    }

}
