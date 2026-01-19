package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 06/12/2024
 * @project hr-cie
 */
public record OrganizationLightVm(
        UUID id,
        String code,
        String name) {
}
