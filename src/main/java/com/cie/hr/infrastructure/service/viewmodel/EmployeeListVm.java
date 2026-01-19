package com.cie.hr.infrastructure.service.viewmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public record EmployeeListVm(
        UUID id,
        String lastname,
        String firstname,
        String employee_number,
        UUID employeeId,
        String email,
        String job,
        UUID status_id,
        String status_name,
        boolean is_manager,
        LocalDateTime evaluated_at,
        String organization_name,
        String employee_pole,
        String employee_grade,
        boolean is_delegate,
        List<DisputeOnlyVm> disputes,
        UUID delegate_by,
        double note,
        String campaignName
) {
}
