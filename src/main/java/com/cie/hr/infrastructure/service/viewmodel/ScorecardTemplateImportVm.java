package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 30/09/2024
 * @project hr-cie
 */
public record ScorecardTemplateImportVm(
        String indicator,
        Double weight,
        String unit,
        Double target,
        String description,
        UUID unitId
) {
}
