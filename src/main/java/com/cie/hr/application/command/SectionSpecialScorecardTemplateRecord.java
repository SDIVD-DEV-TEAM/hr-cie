package com.cie.hr.application.command;

import java.util.List;

/**
 * @author Koty BLEU
 * @created 09/05/2023
 * @project hr
 */
public record SectionSpecialScorecardTemplateRecord(String title, List<LineSpecialScorecardTemplateRecord> lines) {
}
