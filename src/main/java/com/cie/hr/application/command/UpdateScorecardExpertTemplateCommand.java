package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardTemplateUseCases;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardExpertTemplateVm;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public record UpdateScorecardExpertTemplateCommand(ScorecardExpertTemplateVm data) implements Command<ScorecardTemplateUseCases, UUID> {
    @Override
    public UUID execute(ScorecardTemplateUseCases useCase) {
        return useCase.updateScorecardExpertTemplate(this);
    }
}
