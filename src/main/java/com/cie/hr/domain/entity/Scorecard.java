package com.cie.hr.domain.entity;

import com.cie.hr.domain.valueobject.JobEmbedded;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 08/06/2023
 * @project hr-cie
 */
public interface Scorecard {
    UUID id();

    EmployeeDomain manager();

    EmployeeDomain assessed();

    EvaluationScorecardManager scorecardForManagerForm();

    EvaluationScorecardExpert scorecardForExpert();

    Status status();

    Campaign campaign();

    JobEmbedded job();

    LocalDateTime evaluatedAt();

    boolean automaticClosed();

    void setManager(EmployeeDomain manager);

    void setManagerForm(EvaluationScorecardManager scorecardForManagerForm);

    void setExpertForm(EvaluationScorecardExpert scorecardForExpert);

    void setStatus(Status status);

    boolean deleted();

    void setIsDeleted(boolean isDeleted);

    void setJob(JobEmbedded job);

    void setAutomaticClosed(boolean automaticClosed);

    void setEvaluatedAt(LocalDateTime evaluatedAt);
}
