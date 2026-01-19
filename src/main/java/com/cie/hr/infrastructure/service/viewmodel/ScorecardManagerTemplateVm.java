package com.cie.hr.infrastructure.service.viewmodel;

import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record ScorecardManagerTemplateVm(
        UUID id,
        String title,
        String type,
        ScorecardForManagerForm form) {
}
