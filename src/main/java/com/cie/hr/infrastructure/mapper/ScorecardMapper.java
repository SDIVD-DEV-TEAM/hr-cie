package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.infrastructure.entity.*;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 12/05/2023
 * @project hr-cie
 */
public class ScorecardMapper {
    public static ScorecardEntity toEntity(ScorecardDomain scorecard) {
        if (scorecard == null) {
            return null;
        }
        var status = StatusEntity.builder()
                .code(scorecard.getStatus().getCode())
                .name(scorecard.getStatus().getName())
                .build();
        status.setId(scorecard.getStatus().getId());
        status.setDeleted(scorecard.getStatus().getDeleted());

        ScorecardEntity scorecardEntity = ScorecardEntity.builder()
                .expertTemplate(scorecard.getEvaluationScorecardExpert())
                .managerTemplate(scorecard.getEvaluationScorecardManager())
                .status(status)
                .assessed(EmployeeMapper.toEmployeeEntity(scorecard.getAssessed()))
                .manager(EmployeeMapper.toEmployeeEntity(scorecard.getManager()))
                .campaign(CampaignMapper.toCampaignEntity(scorecard.getCampaign()))
                .evaluated_at(scorecard.getEvaluatedAt())
                .automaticClosed(scorecard.automaticClosed())
                .job(JobMapper.toJobEmbeddedEntity(scorecard.getJob()))
                .build();
        scorecardEntity.setId(scorecard.getId());
        scorecardEntity.setDeleted(scorecard.isDeleted());
        return scorecardEntity;
    }

    public static ScorecardDomain toDomain(ScorecardEntity scorecardEntity) {
        if (scorecardEntity == null) {
            return null;
        }
        return ScorecardDomain.newBuilder()
                .id(scorecardEntity.getId())
                .assessed(EmployeeMapper.toEmployeeDomain(scorecardEntity.getAssessed()))
                .campaign(CampaignMapper.toCampaign(scorecardEntity.getCampaign()))
                .evaluatedAt(scorecardEntity.getEvaluated_at())
                .manager(EmployeeMapper.toEmployeeDomain(scorecardEntity.getManager()))
                .scorecardForExpert(scorecardEntity.getExpertTemplate())
                .scorecardForManagerForm(scorecardEntity.getManagerTemplate())
                .status(StatusMapper.toStatusDomain(scorecardEntity.getStatus()))
                .deleted(scorecardEntity.isDeleted())
                .job(JobMapper.toJobEmbedded(scorecardEntity.getJob()))
                .automaticClosed(scorecardEntity.isAutomaticClosed())
                .build();
    }

    public static void updateAndSave(ScorecardDomain domain, ScorecardEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(domain.getAssessed());
        if (employeeEntity != null && (entity.getAssessed() == null ||
                !Objects.equals(entity.getAssessed(), employeeEntity))) {
            entity.setAssessed(employeeEntity);
        }

        CampaignEntity campaignEntity = CampaignMapper.toCampaignEntity(domain.getCampaign());
        if (campaignEntity != null && (entity.getCampaign() == null ||
                !Objects.equals(entity.getCampaign(), campaignEntity))) {
            entity.setCampaign(campaignEntity);
        }

        if (!Objects.equals(domain.getEvaluatedAt(), entity.getEvaluated_at())) {
            entity.setEvaluated_at(domain.getEvaluatedAt());
        }

        EmployeeEntity managerEntity = EmployeeMapper.toEmployeeEntity(domain.getManager());
        if (managerEntity != null && (entity.getManager() == null ||
                !Objects.equals(entity.getManager(), managerEntity))) {
            entity.setManager(managerEntity);
        }

        if (!Objects.equals(domain.getEvaluationScorecardExpert(), entity.getExpertTemplate())) {
            entity.setExpertTemplate(domain.getEvaluationScorecardExpert());
        }

        if (!Objects.equals(domain.getEvaluationScorecardManager(), entity.getManagerTemplate())) {
            entity.setManagerTemplate(domain.getEvaluationScorecardManager());
        }

        StatusEntity statusEntity = StatusMapper.toStatusEntity(domain.getStatus());
        if (statusEntity != null && (entity.getStatus() == null ||
                !Objects.equals(entity.getStatus(), statusEntity))) {
            entity.setStatus(statusEntity);
        }

        if (Objects.equals(domain.isDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.isDeleted());
        }

        JobEmbeddedEntity jobEmbeddedEntity = JobMapper.toJobEmbeddedEntity(domain.job());
        if (jobEmbeddedEntity != null && (entity.getJob() == null ||
                !Objects.equals(entity.getJob(), jobEmbeddedEntity))) {
            entity.setJob(jobEmbeddedEntity);
        }

        if (Objects.equals(domain.isAutomaticClosed(), entity.isAutomaticClosed())) {
            entity.setAutomaticClosed(domain.isAutomaticClosed());
        }
    }
}
