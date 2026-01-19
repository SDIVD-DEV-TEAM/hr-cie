package com.cie.hr.infrastructure.valueobject;

import java.util.List;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record FormSpecialSection(
        String title,
        Double note,
        String type,
        List<FormSpecialLine> lines,
        Double coefficient,
        boolean completed
) {
}
