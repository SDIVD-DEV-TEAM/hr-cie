package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 05/10/2023
 * @project hr-cie
 */
public record DisputeOnlyVm(
        UUID id,
        ScorecardVm scorecard
) {
}
