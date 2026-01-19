package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Status;

import java.util.Optional;
import java.util.UUID;

public interface StatusRepositoryPort extends AbstractRepository<Status, UUID> {
    Optional<Status> findByCode(String code);

    Optional<Status> findByName(String name);
}
