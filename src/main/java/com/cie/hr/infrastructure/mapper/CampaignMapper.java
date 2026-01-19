package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Campaign;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.service.viewmodel.CampaignVm;

import java.util.Objects;

public class CampaignMapper {
    public static CampaignEntity toCampaignEntity(Campaign campaign) {
        if (campaign == null) {
            return null;
        }
        CampaignEntity campaignEntity = CampaignEntity.builder()
                .startDate(campaign.getStartDate())
                .endDate(campaign.getEndDate())
                .status(StatusMapper.toStatusEntity(campaign.getStatus()))
                .name(campaign.getName())
                .build();
        campaignEntity.setId(campaign.getId());
        campaignEntity.setDeleted(campaign.isDeleted());
        return campaignEntity;
    }

    public static Campaign toCampaign(CampaignEntity campaignEntity) {
        if (campaignEntity == null) {
            return null;
        }
        return new Campaign(
                campaignEntity.getId(),
                campaignEntity.getName(),
                campaignEntity.getStartDate(),
                campaignEntity.getEndDate(),
                StatusMapper.toStatusDomain(campaignEntity.getStatus()),
                campaignEntity.isDeleted()
        );
    }

    public static CampaignVm toCampaignVm(CampaignEntity campaignEntity) {
        if (campaignEntity == null) {
            return null;
        }
        return new CampaignVm(
                campaignEntity.getId(),
                campaignEntity.getName(),
                campaignEntity.getStartDate(),
                campaignEntity.getEndDate(),
                StatusMapper.toStatusVm(campaignEntity.getStatus()),
                false
        );

    }

    public static void updateAndSave(Campaign domain, CampaignEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getEndDate(), entity.getEndDate())) {
            entity.setEndDate(domain.getEndDate());
        }

        if (!Objects.equals(domain.getStartDate(), entity.getStartDate())) {
            entity.setStartDate(domain.getStartDate());
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        StatusEntity statusEntity = StatusMapper.toStatusEntity(domain.getStatus());
        if (statusEntity != null && (entity.getStatus() == null ||
                !Objects.equals(entity.getStatus(), statusEntity))) {
            entity.setStatus(statusEntity);
        }

        if (!Objects.equals(domain.isDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.isDeleted());
        }
    }
}
