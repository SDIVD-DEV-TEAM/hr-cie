package com.cie.hr.domain.port;

import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;

import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 07/07/2023
 * @project hr-cie
 */
public interface ScorecardManagerTemplateRepositoryPort {
    Optional<ScorecardForManagerForm> findFirstByTypeAndActiveTrue(Integer type);
}
