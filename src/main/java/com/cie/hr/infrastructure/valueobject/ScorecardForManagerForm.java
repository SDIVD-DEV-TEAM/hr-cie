package com.cie.hr.infrastructure.valueobject;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record ScorecardForManagerForm(
        FormStandardSection sectionA,
        FormStandardSection sectionB,
        FormStandardSection sectionC,
        FormSpecialSection sectionD,
        FormPropositionSection sectionE,
        FormAnalyseSection sectionF
) implements ScorecardTemplate {
}
