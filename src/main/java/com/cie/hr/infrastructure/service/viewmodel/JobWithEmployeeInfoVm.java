package com.cie.hr.infrastructure.service.viewmodel;

import com.cie.hr.infrastructure.valueobject.FormSpecialSection;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 21/05/2023
 * @project hr
 */
public record JobWithEmployeeInfoVm(
        UUID employeeId,
        String employeeFirstname,
        String employeeLastname,
        UUID organizationId,
        String organizationCode,
        String organizationName,
        UUID jobId,
        String jobTitle,
        String jobCode,
        String grade,
        FormSpecialSection evaluationForm
) {
}
