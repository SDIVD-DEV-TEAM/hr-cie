package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.NoteDistribution;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public interface NoteDistributionRepositoryPort extends AbstractRepository<NoteDistribution, UUID> {
    Optional<NoteDistribution> findByCode(String code);
}
