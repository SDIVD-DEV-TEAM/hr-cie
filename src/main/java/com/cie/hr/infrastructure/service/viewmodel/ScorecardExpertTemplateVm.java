package com.cie.hr.infrastructure.service.viewmodel;

import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record ScorecardExpertTemplateVm(UUID id, String title, String type, ScorecardForExpert form) {
}
