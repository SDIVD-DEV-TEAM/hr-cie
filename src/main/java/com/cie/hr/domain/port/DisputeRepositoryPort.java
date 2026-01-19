package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Disputes;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
public interface DisputeRepositoryPort extends AbstractRepository<Disputes, UUID>{

    Optional<Disputes> findByScorecardId(UUID scorecardId);
}
