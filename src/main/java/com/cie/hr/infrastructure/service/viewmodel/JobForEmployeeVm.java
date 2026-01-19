package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 10/10/2024
 * @project hr-cie
 */
public record JobForEmployeeVm(
        UUID id,
        String title,
        PoleVm pole
) {
}
