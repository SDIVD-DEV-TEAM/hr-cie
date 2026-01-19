package com.cie.hr.domain.valueobject;

/**
 * @author Alexis TAMBIE
 * @created 19/07/2023
 * @project hr-cie
 */

public record JobEmbedded(
        String title,
        String code,
        String organization,
        String grade,
        String organization_type
) {
}
