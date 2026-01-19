package com.cie.hr.infrastructure.service.viewmodel;

/**
 * @author Alexis TAMBIE
 * @created 17/07/2023
 * @project hr-cie
 */
public record EvaluationVm(
        String performance_range,
        Double note,
        int year
) {
}
