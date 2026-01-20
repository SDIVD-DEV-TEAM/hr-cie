package com.cie.hr.infrastructure.mapper;

import java.util.Objects;

import com.cie.hr.domain.entity.Organization;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.service.viewmodel.OrganizationLightVm;
import com.cie.hr.infrastructure.service.viewmodel.OrganizationVm;

public class OrganizationMapper {

    public static OrganizationVm toOrganizationVm(OrganizationEntity organizationEntity) {
        if (organizationEntity == null) {
            return null;
        }

        return new OrganizationVm(
                organizationEntity.getId(),
                organizationEntity.getName(),
                organizationEntity.getCode(),
                toOrganizationLightVm(organizationEntity.getParent()),
                OrganizationTypeMapper.toOrganizationVm(organizationEntity.getType()),
                organizationEntity.getChiefJob() == null || organizationEntity.getChiefJob().getEmployee() == null ? "" :
                                organizationEntity.getChiefJob().getEmployee().getFullName(),
                organizationEntity.getCostCenter(),
                organizationEntity.getChiefOrganization(),
                organizationEntity.getShortCode()

        );
    }

    public static OrganizationEntity toOrganizationEntity(Organization organization) {
        if (organization == null) {
            return null;
        }

        var organizationEntity = OrganizationEntity.builder()
                .name(organization.getName())
                .code(organization.getCode())
                .parent(OrganizationMapper.toOrganizationEntity(organization.getParent()))
                .type(OrganizationTypeMapper.toEntity(organization.getType()))
                .costCenter(organization.getCostCenter())
                .shortCode(organization.getShortCode())
                .build();
        organizationEntity.setId(organization.getId());
        return organizationEntity;
    }


    public static Organization toOrganization(OrganizationEntity organizationEntity) {
        if (organizationEntity == null) {
            return null;
        }
        Organization organization = new Organization(
                organizationEntity.getId(),
                organizationEntity.getName(),
                organizationEntity.getCode(),
                OrganizationMapper.toOrganization(organizationEntity.getParent()),
                OrganizationTypeMapper.toDomain(organizationEntity.getType()),
                organizationEntity.getCostCenter(),
                organizationEntity.getShortCode()
        );
        organization.setId(organizationEntity.getId());
        return organization;
    }

    public static OrganizationLightVm toOrganizationLightVm(OrganizationEntity entity) {
        if (entity == null) {
            return null;
        }
        return new OrganizationLightVm(
                entity.getId(),
                entity.getCode(),
                entity.getName()
        );
    }

    public static void updateAndSave(Organization domain, OrganizationEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        OrganizationTypeEntity organizationTypeEntity = OrganizationTypeMapper.toEntity(domain.getType());
        if (organizationTypeEntity != null && (entity.getType() == null ||
                !Objects.equals(entity.getType(), organizationTypeEntity))) {
            entity.setType(organizationTypeEntity);
        }

        if (!Objects.equals(domain.getCostCenter(), entity.getCostCenter())) {
            entity.setCostCenter(domain.getCostCenter());
        }

        OrganizationEntity organizationEntity = OrganizationMapper.toOrganizationEntity(domain.getParent());
        if (organizationEntity != null && (entity.getParent() == null ||
                !Objects.equals(entity.getParent(), organizationEntity))) {
            entity.setParent(organizationEntity);
        }

        if (!Objects.equals(domain.getShortCode(), entity.getShortCode())) {
            entity.setShortCode(domain.getShortCode());
        }
    }
}
