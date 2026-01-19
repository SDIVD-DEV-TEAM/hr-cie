package com.cie.hr.application.command;

import java.util.List;

/**
 * @author Koty BLEU
 * @created 09/05/2023
 * @project hr
 */
public record SectionStandardScorecardTemplateRecord(String title, List<LineStandardScorecardTemplateRecord> lines) {
}
