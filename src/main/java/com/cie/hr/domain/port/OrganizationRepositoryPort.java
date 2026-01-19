package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.entity.Organization;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepositoryPort extends AbstractRepository<Organization, UUID> {

    boolean checkNameOrCodeAlreadyExists(String name, String code);

    Job findChiefJob(UUID organizationId);

    Optional<Organization> findByCode(String code);
}
