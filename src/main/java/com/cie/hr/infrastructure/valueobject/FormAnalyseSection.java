package com.cie.hr.infrastructure.valueobject;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
public record FormAnalyseSection(
        String title,
        String type,
        List<FormAnalyseLine> lines,
        boolean completed
) {
}
