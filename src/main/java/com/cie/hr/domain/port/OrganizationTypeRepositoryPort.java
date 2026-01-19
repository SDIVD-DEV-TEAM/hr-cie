package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.OrganizationType;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationTypeRepositoryPort extends AbstractRepository<OrganizationType, UUID> {
    Optional<Boolean> checkChildAndParentOrganizationType(UUID childTypeId, UUID parentId);
}
