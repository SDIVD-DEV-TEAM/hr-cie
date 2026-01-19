package com.cie.hr.infrastructure.valueobject;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record ScorecardForExpert(
        FormStandardSection sectionA,
        FormSpecialSection sectionB,
        FormPropositionSection sectionC,
        FormAnalyseSection sectionD
) implements ScorecardTemplate {
}
