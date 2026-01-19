package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Delegation;
import com.cie.hr.infrastructure.entity.CampaignEntity;
import com.cie.hr.infrastructure.entity.DelegationEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;

import java.util.Objects;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public class DelegationMapper {
    public static Delegation toDomain(DelegationEntity entity) {
        return Delegation.builder().id(entity.getId())
                .employee(EmployeeMapper.toEmployeeDomain(entity.getEmployee()))
                .campaign(CampaignMapper.toCampaign(entity.getCampaign()))
                .deleted(entity.isDeleted())
                .receiver(EmployeeMapper.toEmployeeDomain(entity.getReceiver()))
                .giver(EmployeeMapper.toEmployeeDomain(entity.getGiver()))
                .reason(entity.getReason())
                .build();
    }
    public static  DelegationEntity toEntity(Delegation domain) {

        var delegation = DelegationEntity.builder()
                .employee(EmployeeMapper.toEmployeeEntity(domain.getEmployee()))
                .campaign(CampaignMapper.toCampaignEntity(domain.getCampaign()))
                .receiver(EmployeeMapper.toEmployeeEntity(domain.getReceiver()))
                .giver(EmployeeMapper.toEmployeeEntity(domain.getGiver()))
                .reason(domain.getReason())
                .build();
        delegation.setId(domain.getId());
        delegation.setDeleted(delegation.isDeleted());
        return  delegation;
    }

    public static void updateAndSave(Delegation domain, DelegationEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(domain.getEmployee());
        if (employeeEntity != null && (entity.getEmployee() == null ||
                !Objects.equals(entity.getEmployee(), employeeEntity))) {
            entity.setEmployee(employeeEntity);
        }

        EmployeeEntity receiverEmployee = EmployeeMapper.toEmployeeEntity(domain.getReceiver());
        if (receiverEmployee != null && (entity.getReceiver() == null ||
                !Objects.equals(entity.getReceiver(), receiverEmployee))) {
            entity.setReceiver(receiverEmployee);
        }

        EmployeeEntity giverEmployee = EmployeeMapper.toEmployeeEntity(domain.getGiver());
        if (giverEmployee != null && (entity.getGiver() == null ||
                !Objects.equals(entity.getGiver(), giverEmployee))) {
            entity.setGiver(giverEmployee);
        }

        CampaignEntity campaignEntity = CampaignMapper.toCampaignEntity(domain.getCampaign());
        if (campaignEntity != null && (entity.getCampaign() == null ||
                !Objects.equals(entity.getCampaign(), campaignEntity))) {
            entity.setCampaign(campaignEntity);
        }


        if (!Objects.equals(domain.getReason(), entity.getReason())) {
            entity.setReason(domain.getReason());
        }

        if (!Objects.equals(domain.getDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.getDeleted());
        }
    }
}
