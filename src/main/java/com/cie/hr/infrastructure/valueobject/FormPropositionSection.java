package com.cie.hr.infrastructure.valueobject;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
public record FormPropositionSection(
        String title,
        String type,
        List<FormPropositionLine> lines,
        boolean completed
) {
}
