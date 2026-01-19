package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.ScoreNoteDistribution;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public interface ScoreNoteDistributionRepositoryPort extends AbstractRepository<ScoreNoteDistribution, UUID> {
    Optional<ScoreNoteDistribution> findFirstByValueInRangeAndNoteDistributionId(double value, UUID id);
}
