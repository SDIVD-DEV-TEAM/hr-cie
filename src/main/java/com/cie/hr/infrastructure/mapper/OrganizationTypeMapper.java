package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.OrganizationType;
import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.service.viewmodel.OrganizationTypeVm;

import java.util.Objects;

public class OrganizationTypeMapper {
    public static OrganizationType toDomain(OrganizationTypeEntity organizationTypeEntity) {
        if (organizationTypeEntity == null) {
            return null;
        }
        return new OrganizationType(
                organizationTypeEntity.getId(),
                organizationTypeEntity.getName(),
                organizationTypeEntity.getCode(),
                organizationTypeEntity.getGradeCode()
        );
    }

    public static OrganizationTypeVm toOrganizationVm(OrganizationTypeEntity organizationTypeEntity) {
        if (organizationTypeEntity == null) {
            return null;
        }
        return new OrganizationTypeVm(
                organizationTypeEntity.getId(),
                organizationTypeEntity.getName(),
                organizationTypeEntity.getCode());
    }

    public static OrganizationTypeEntity toEntity(OrganizationType organizationType) {
        if (organizationType == null) {
            return null;
        }
        OrganizationTypeEntity type = new OrganizationTypeEntity(
                organizationType.getName(),
                organizationType.getCode(),
                organizationType.getGradeCode());
        type.setId(organizationType.getId());
        return type;
    }

    public static void updateAndSave(OrganizationType domain, OrganizationTypeEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        if (!Objects.equals(domain.getGradeCode(), entity.getGradeCode())) {
            entity.setGradeCode(domain.getGradeCode());
        }
    }
}
