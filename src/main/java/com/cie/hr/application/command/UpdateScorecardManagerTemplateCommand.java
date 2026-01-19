package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardTemplateUseCases;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardManagerTemplateVm;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public record UpdateScorecardManagerTemplateCommand(ScorecardManagerTemplateVm data) implements Command<ScorecardTemplateUseCases, UUID> {
    @Override
    public UUID execute(ScorecardTemplateUseCases useCase) {
        return useCase.updateScorecardManagerTemplate(this);
    }
}
