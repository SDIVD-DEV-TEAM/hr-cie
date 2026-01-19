package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.InfrastructureException;
import com.cie.hr.domain.entity.OrganizationType;
import com.cie.hr.domain.port.OrganizationTypeRepositoryPort;
import com.cie.hr.infrastructure.entity.OrganizationTypeEntity;
import com.cie.hr.infrastructure.mapper.OrganizationTypeMapper;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import com.cie.hr.infrastructure.repository.OrganizationTypeJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class OrganizationTypeRepositoryAdapter implements OrganizationTypeRepositoryPort {

    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    private final OrganizationJpaRepository organizationJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public OrganizationTypeRepositoryAdapter(OrganizationTypeJpaRepository organizationTypeJpaRepository, OrganizationJpaRepository organizationJpaRepository) {
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
    }

    @Override
    public Optional<Boolean> checkChildAndParentOrganizationType(UUID childTypeId, UUID parentId) {
        var childType = organizationTypeJpaRepository.findById(childTypeId);
        if (childType.isEmpty())
            throw new InfrastructureException("Ce type n'existe pas");
        int childTypeCodeInt = Integer.parseInt(childType.get().getCode());
        var parentOrganization = organizationJpaRepository.findById(parentId);
        if (parentOrganization.isEmpty())
            throw new InfrastructureException("Cette organisation n'existe pas ");

        var parentType = parentOrganization.get().getType();
        int parentTypeCodeInt = Integer.parseInt(parentType.getCode());

        return Optional.of(!(childTypeCodeInt < parentTypeCodeInt || (childTypeCodeInt == parentTypeCodeInt) && parentOrganization.get().getParent() != null));
    }

    @Override
    public void save(OrganizationType domain) {
        organizationTypeJpaRepository.save(OrganizationTypeMapper.toEntity(domain));
    }

    @Override
    public Optional<OrganizationType> findById(UUID id) {
        var foundOrganizationType = organizationTypeJpaRepository.findById(id);
        return foundOrganizationType.map(OrganizationTypeMapper::toDomain);
    }

    @Override
    public void updateAndSave(OrganizationType domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                OrganizationTypeEntity organisationTypeEntity = organizationTypeJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette organisation n'existe pas"));
                OrganizationTypeMapper.updateAndSave(domain, organisationTypeEntity);
                organizationTypeJpaRepository.save(organisationTypeEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update organization after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating organization", e);
                throw e;
            }
        }
    }
}
