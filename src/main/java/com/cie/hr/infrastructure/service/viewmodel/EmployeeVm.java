package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public record EmployeeVm(
        UUID id,
        String lastname,
        String firstname,
        String employee_number,
        String email,
        String profile,
        String job,
        String grade,
        ProfileVm profileObj,
        String access_level,
        JobForEmployeeVm jobObj,
        boolean active
) {
}
