package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 22/05/2023
 * @project hr
 */
public record ScorecardVm(
        UUID id,
        String campaignTitle,
        String manager
) {
}
