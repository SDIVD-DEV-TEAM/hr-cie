package com.cie.hr.domain.port;

import com.cie.hr.domain.valueobject.Formation;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
public interface FormationRepositoryPort extends AbstractRepository<Formation, UUID> {

    Optional<Formation> findById(UUID id);
}
