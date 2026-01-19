package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardUseCases;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 25/05/2023
 * @project hr-cie
 */
public record UpdateScoreCardCommand(
        UUID scorecardId,
        UUID managerId,
        EvaluationScorecardManager scorecardManager,
        EvaluationScorecardExpert scorecardExpert
) implements Command<ScorecardUseCases, UUID> {
    @Override
    public UUID execute(ScorecardUseCases useCase) throws MessagingException, IOException {
        return useCase.updateScorecard(this);
    }
}
