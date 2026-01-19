package com.cie.hr.infrastructure.valueobject;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record FormStandardLine(
        String title,
        Double coefficient,
        Double note,
        String comments
) {
}
