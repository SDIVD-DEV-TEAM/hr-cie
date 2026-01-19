package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.entity.Organization;
import com.cie.hr.domain.port.OrganizationRepositoryPort;
import com.cie.hr.infrastructure.entity.OrganizationEntity;
import com.cie.hr.infrastructure.mapper.JobMapper;
import com.cie.hr.infrastructure.mapper.OrganizationMapper;
import com.cie.hr.infrastructure.repository.OrganizationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class OrganizationRepositoryAdapter implements OrganizationRepositoryPort {

    private final OrganizationJpaRepository organizationJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public OrganizationRepositoryAdapter(OrganizationJpaRepository organizationJpaRepository) {
        this.organizationJpaRepository = organizationJpaRepository;
    }

    @Override
    public void save(Organization entity) {
        OrganizationEntity organizationEnt = OrganizationMapper.toOrganizationEntity(entity);
        this.organizationJpaRepository.save(organizationEnt);
    }

    @Override
    public Optional<Organization> findById(UUID id) {
        return this.organizationJpaRepository.findById(id).map(OrganizationMapper::toOrganization);
    }

    @Override
    public void updateAndSave(Organization domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                OrganizationEntity organisationEntity = organizationJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette organisation n'existe pas"));
                OrganizationMapper.updateAndSave(domain, organisationEntity);
                organizationJpaRepository.save(organisationEntity);
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

    @Override
    public boolean checkNameOrCodeAlreadyExists(String name, String code) {
        return !organizationJpaRepository.findByNameOrCode(name, code).isEmpty();
    }

    @Override
    public Job findChiefJob(UUID organizationId) {
        Optional<OrganizationEntity> organization = organizationJpaRepository.findById(organizationId);
        if (organization.isEmpty()) {
            return null;
        }
        return organization.map(organizationEntity -> JobMapper.toJobDomain(organizationEntity.getChiefJob())).orElse(null);
    }

    @Override
    public Optional<Organization> findByCode(String code) {
        return organizationJpaRepository.findFirstByCode(code).map(OrganizationMapper::toOrganization);
    }
}
