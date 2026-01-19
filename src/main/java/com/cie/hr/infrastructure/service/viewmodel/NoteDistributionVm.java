package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public record NoteDistributionVm(
        UUID id,
        String code,
        String description,
        String category,
        String way,
        boolean mayExceed,
        double maxWhenMayExceed,
        boolean allOrNothing
) {
}
