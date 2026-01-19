package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public record OrganizationVm (
        UUID id,
        String name,
        String code,
        OrganizationLightVm parent,
        OrganizationTypeVm type,
        String managerName,
        String costCenter,
        String poleName,
        String shortCode
) {
}
