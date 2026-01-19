package com.cie.hr.infrastructure.valueobject;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
public record FormPropositionLine(
        String title,
        boolean proposition,
        String proposition_value,
        String proposition_reason
) {
}
