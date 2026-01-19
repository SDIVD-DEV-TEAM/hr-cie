package com.cie.hr.domain.port;

import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;

import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 07/07/2023
 * @project hr-cie
 */
public interface ScorecardExpertTemplateRepositoryPort {
    Optional<ScorecardForExpert> findFirstByTypeAndActiveTrue(Integer type);
}
