package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 11/09/2023
 * @project hr-cie
 */
public record EmployeeWithJobList(
        UUID id,
        String lastname,
        String firstname,
        String employee_number,
        String email,
        String profile,
        String job,
        String grade
) {
}
