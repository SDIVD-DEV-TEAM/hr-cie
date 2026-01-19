package com.cie.hr.infrastructure.service.viewmodel;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 24/05/2023
 * @project hr-cie
 */
public record EmployeePerformanceVm(
        UUID scorecardId,
        String campaignTitle,
        String manager,
        String lastname,
        String firstname,
        String employee_number,
        UUID employeeId,
        String email,
        String job,
        UUID status_id,
        String status_name,
        Double note,
        LocalDateTime evaluated_at,
        Date start_campaign,
        Date end_campaign,
        String employee_pole,
        String organization_name,
        String employee_grade,
        boolean canBeEvaluated,
        boolean canBeDerogated
) {
}
