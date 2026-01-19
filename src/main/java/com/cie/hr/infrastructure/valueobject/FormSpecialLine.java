package com.cie.hr.infrastructure.valueobject;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record FormSpecialLine(
        String title,
        Double coefficient,
        Double note,
        Double objective,
        Double achieved,
        String unit,
        UUID noteId
) {
}
