package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardUseCases;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public record CreateScorecardCommand(
        UUID assessedId,
        UUID templateId,
        UUID status,
        UUID campaign
) implements Command<ScorecardUseCases, UUID> {
    @Override
    public UUID execute(ScorecardUseCases useCase) throws MessagingException, IOException {
        return useCase.createScorecard(this);
    }
}
