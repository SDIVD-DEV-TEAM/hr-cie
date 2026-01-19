package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Derogation;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.DerogationEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;

import java.util.Objects;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public class DerogationMapper {
    public static Derogation toDomain(DerogationEntity entity) {
        if (entity == null) {
            return null;
        }
        return Derogation.builder()
                .id(entity.getId())
                .employee(EmployeeMapper.toEmployeeDomain(entity.getEmployee()))
                .manager(EmployeeMapper.toEmployeeDomain(entity.getManger()))
                .campaign(CampaignMapper.toCampaign(entity.getCampaign()))
                .expiredAt(entity.getExpiredAt())
                .deleted(entity.isDeleted())
                .build();
    }

    public static DerogationEntity toEntity(Derogation domain) {
        if (domain == null) {
            return null;
        }

        var derogation = DerogationEntity.builder()
                .employee(EmployeeMapper.toEmployeeEntity(domain.getEmployee()))
                .manger(EmployeeMapper.toEmployeeEntity(domain.getManager()))
                .campaign(CampaignMapper.toCampaignEntity(domain.getCampaign()))
                .expiredAt(domain.getExpiredAt())
                .build();
        derogation.setId(domain.getId());
        derogation.setDeleted(derogation.isDeleted());
        return derogation;
    }

    public static void updateAndSave(Derogation domain, DerogationEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(domain.getEmployee());
        if (employeeEntity != null && (entity.getEmployee() == null ||
                !Objects.equals(entity.getEmployee(), employeeEntity))) {
            entity.setEmployee(employeeEntity);
        }

        EmployeeEntity managerEntity = EmployeeMapper.toEmployeeEntity(domain.getManager());
        if (managerEntity != null && (entity.getManger() == null ||
                !Objects.equals(entity.getManger(), managerEntity))) {
            entity.setManger(managerEntity);
        }

        CampaignEntity campaignEntity = CampaignMapper.toCampaignEntity(domain.getCampaign());
        if (campaignEntity != null && (entity.getCampaign() == null ||
                !Objects.equals(entity.getCampaign(), campaignEntity))) {
            entity.setCampaign(campaignEntity);
        }

        if (!Objects.equals(entity.getExpiredAt(), domain.getExpiredAt())) {
            entity.setExpiredAt(domain.getExpiredAt());
        }

        if (!Objects.equals(entity.isDeleted(), domain.isDeleted())) {
            entity.setDeleted(domain.isDeleted());
        }
    }
}
