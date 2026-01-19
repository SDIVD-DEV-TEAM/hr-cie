package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardUseCases;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 29/01/2025
 * @project hr-cie
 */
public record ReCreatedScorecardCommand(
        UUID assessedId,
        UUID campaign
) implements Command<ScorecardUseCases, UUID> {
    @Override
    public UUID execute(ScorecardUseCases useCase){
        return useCase.reCreatedScorecard(this);
    }
}

