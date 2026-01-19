package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ScorecardUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/06/2023
 * @project hr-cie
 */
public record CloseScoreCardCommand(
        @NotNull(message = "L'identifiant de la fiche est obligatoire")
        UUID scorecardId
) implements Command<ScorecardUseCases, UUID> {
    @Override
    public UUID execute(ScorecardUseCases useCase) throws MessagingException, IOException {
        return useCase.closeScorecard(this);
    }
}
