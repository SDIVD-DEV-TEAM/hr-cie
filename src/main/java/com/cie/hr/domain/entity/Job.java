package com.cie.hr.domain.entity;

import java.util.UUID;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import com.fasterxml.uuid.Generators;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
public class Job {
    private final UUID id;
    private String title;
    private String code;
    private Grade grade;
    private Organization organizationId;
    private UUID parentId;
    private EmployeeDomain employeeId;
    private boolean isDeleted;
    private FormSpecialSection jobTemplate;

    private Job(Builder builder) {
        id = builder.id == null ? Generators.timeBasedEpochGenerator().generate() : builder.id;
        title = builder.title;
        code = builder.code;
        grade = builder.grade;
        organizationId = builder.organizationId;
        parentId = builder.parentId;
        employeeId = builder.employeeId;
        isDeleted = builder.isDeleted;
        jobTemplate = builder.jobTemplate;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public static final class Builder {
        private UUID id;
        private String title;
        private String code;
        private Grade grade;
        private Organization organizationId;
        private UUID parentId;
        private EmployeeDomain employeeId;
        private boolean isDeleted;
        private FormSpecialSection jobTemplate;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder grade(Grade val) {
            grade = val;
            return this;
        }

        public Builder organizationId(Organization val) {
            organizationId = val;
            return this;
        }

        public Builder parentId(UUID val) {
            parentId = val;
            return this;
        }

        public Builder employeeId(EmployeeDomain val) {
            employeeId = val;
            return this;
        }

        public Builder isDeleted(boolean val) {
            isDeleted = val;
            return this;
        }

        public Builder jobTemplate(FormSpecialSection val) {
            jobTemplate = val;
            return this;
        }

        public Job build() {
            return new Job(this);
        }
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void checkBusinessRules(JobRepositoryPort jobRepositoryPort, EmployeeRepositoryPort employeeRepository) {

        // checks if code is already used
        if (jobRepositoryPort.existsByCode(code))
            throw new DomainException("Job with code : " + code + " already exists");

        // checks if job parent exists
        if (parentId != null && jobRepositoryPort.existsById(parentId).isEmpty())
            throw new DomainException("Job parent with : " + parentId + " not exists");

        // checks if employee exists is not null
        if (employeeId != null && employeeRepository.existsById(employeeId.id()).isEmpty())
            throw new DomainException("employee with id : " + employeeId.id() + " not exists");

    }

    public void checkBusinessRulesOnUpdate(JobRepositoryPort jobRepositoryPort, EmployeeRepositoryPort employeeRepository) {

        // checks if employee exists is not null
        if (employeeId != null && employeeRepository.existsById(employeeId.id()).isEmpty())
            throw new DomainException("employee with id : " + employeeId.id() + " not exists");

        // checking if job exists
        Job jobFromDb = jobRepositoryPort.findById(id).orElseThrow(() -> new DomainException("Job id : " + id + " is not found"));

        // checking if code is not used by another job
        if (!jobFromDb.getCode().equals(code) && jobRepositoryPort.existsByCode(code)) {
            throw new DomainException("Job with code : " + code + " already exists");
        }
    }

    public void checkBusinessRulesOnDelete(JobRepositoryPort jobRepositoryPort) {

        var job = jobRepositoryPort.findById(id).orElseThrow(DomainException::new);
        // checking if the job is not a parent of an another
        if (!jobRepositoryPort.findByParentId(id).isEmpty())
            throw new DomainException("One or Many job(s) belongs to job id : " + id);

        // checking if post has no active employee
        if (job.employeeId != null)
            throw new DomainException("job with id : " + id + "is occupied by employee with id : " + job.employeeId);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", grade=" + grade +
                ", organizationId=" + organizationId +
                ", parentId=" + parentId +
                ", employeeId=" + employeeId +
                ", isDeleted=" + isDeleted +
                ", jobTemplate=" + jobTemplate +
                '}';
    }
}
