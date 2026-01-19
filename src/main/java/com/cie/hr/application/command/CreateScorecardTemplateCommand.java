package com.cie.hr.application.command;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
public record CreateScorecardTemplateCommand(
        String name,
        Boolean employeeForm,
        SectionStandardScorecardTemplateRecord sectionA,
        SectionStandardScorecardTemplateRecord sectionB,
        SectionStandardScorecardTemplateRecord sectionC,
        SectionSpecialScorecardTemplateRecord sectionD
) {
}
