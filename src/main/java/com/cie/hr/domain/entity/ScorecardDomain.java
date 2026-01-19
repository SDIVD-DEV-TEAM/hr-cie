package com.cie.hr.domain.entity;

import com.cie.hr.domain.valueobject.JobEmbedded;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
public class ScorecardDomain implements Scorecard {
    private final UUID id;
    private EmployeeDomain manager;
    private final EmployeeDomain assessed;
    private EvaluationScorecardManager scorecardForManagerForm;
    private EvaluationScorecardExpert scorecardForExpert;
    private Status status;
    private final Campaign campaign;
    private LocalDateTime evaluatedAt;
    private boolean deleted;
    private JobEmbedded job;
    private boolean automaticClosed;

    public EvaluationScorecardManager getEvaluationScorecardManager() {
        return scorecardForManagerForm;
    }

    public EvaluationScorecardExpert getEvaluationScorecardExpert() {
        return scorecardForExpert;
    }

    private ScorecardDomain(Builder builder) {
        id = builder.id;
        manager = builder.manager;
        assessed = builder.assessed;
        scorecardForManagerForm = builder.scorecardForManagerForm;
        scorecardForExpert = builder.scorecardForExpert;
        status = builder.status;
        campaign = builder.campaign;
        evaluatedAt = builder.evaluatedAt;
        deleted = builder.deleted;
        job = builder.job;
        automaticClosed = builder.automaticClosed;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public EmployeeDomain manager() {
        return manager;
    }

    @Override
    public EmployeeDomain assessed() {
        return assessed;
    }

    @Override
    public EvaluationScorecardManager scorecardForManagerForm() {
        return scorecardForManagerForm;
    }

    @Override
    public EvaluationScorecardExpert scorecardForExpert() {
        return scorecardForExpert;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Campaign campaign() {
        return campaign;
    }

    @Override
    public JobEmbedded job() {
        return job;
    }

    @Override
    public LocalDateTime evaluatedAt() {
        return evaluatedAt;
    }

    @Override
    public boolean automaticClosed() {
        return automaticClosed;
    }

    @Override
    public void setManager(EmployeeDomain manager) {
        this.manager = manager;
    }

    @Override
    public void setManagerForm(EvaluationScorecardManager scorecardForManagerForm) {
        this.scorecardForManagerForm = scorecardForManagerForm;
    }

    @Override
    public void setExpertForm(EvaluationScorecardExpert scorecardForExpert) {
        this.scorecardForExpert = scorecardForExpert;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean deleted() {
        return deleted;
    }

    @Override
    public void setIsDeleted(boolean isDeleted) {
        this.deleted = isDeleted;
    }

    @Override
    public void setJob(JobEmbedded job) {
        this.job = job;
    }

    @Override
    public void setAutomaticClosed(boolean automaticClosed) {
        this.automaticClosed = automaticClosed;
    }

    @Override
    public void setEvaluatedAt(LocalDateTime evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }


    public static final class Builder {
        private UUID id;
        private EmployeeDomain manager;
        private EmployeeDomain assessed;
        private EvaluationScorecardManager scorecardForManagerForm;
        private EvaluationScorecardExpert scorecardForExpert;
        private Status status;
        private Campaign campaign;
        private LocalDateTime evaluatedAt;
        private boolean deleted;
        private JobEmbedded job;
        private boolean automaticClosed;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder manager(EmployeeDomain val) {
            manager = val;
            return this;
        }

        public Builder assessed(EmployeeDomain val) {
            assessed = val;
            return this;
        }

        public Builder scorecardForManagerForm(EvaluationScorecardManager val) {
            scorecardForManagerForm = val;
            return this;
        }

        public Builder scorecardForExpert(EvaluationScorecardExpert val) {
            scorecardForExpert = val;
            return this;
        }

        public Builder status(Status val) {
            status = val;
            return this;
        }

        public Builder deleted(boolean val) {
            deleted = val;
            return this;
        }

        public Builder campaign(Campaign val) {
            campaign = val;
            return this;
        }

        public Builder job(JobEmbedded val) {
            job = val;
            return this;
        }

        public Builder evaluatedAt(LocalDateTime val) {
            evaluatedAt = val;
            return this;
        }

        public Builder automaticClosed(boolean val) {
            automaticClosed = val;
            return this;
        }

        public ScorecardDomain build() {
            return new ScorecardDomain(this);
        }
    }

    public void saveTemplate(Object template, int type) {
        if (type == 2) {
            EvaluationScorecardManager scorecard = (EvaluationScorecardManager) template;
            setManagerForm(scorecard);
        } else {
            EvaluationScorecardExpert scorecard = (EvaluationScorecardExpert) template;
            setExpertForm(scorecard);
        }
    }
}
