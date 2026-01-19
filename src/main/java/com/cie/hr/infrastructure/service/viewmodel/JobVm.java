package com.cie.hr.infrastructure.service.viewmodel;

import com.cie.hr.infrastructure.valueobject.FormSpecialSection;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
public record JobVm(
        UUID id,
        String title,
        String code,
        FormSpecialSection evaluation_form,
        String organizationCode,
        GradeVm grade,
        LightEmployeeVm chief,
        LightEmployeeVm employee,
        OrganizationLightVm organization,
        OrganizationTypeVm organizationType,
        OrganizationTypeVm rattachement
) {
}
