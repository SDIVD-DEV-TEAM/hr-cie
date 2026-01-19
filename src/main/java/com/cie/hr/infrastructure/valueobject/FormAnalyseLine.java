package com.cie.hr.infrastructure.valueobject;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
public record FormAnalyseLine(
        String main_difficulty,
        String reason,
        String possible_improvements,
        String manager_comment
) {
}
