package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.DisputesUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
public record CreateDisputeCommand(
        @NotNull(message = "Le sujet est obligatoire")
        String subject,
        @NotNull(message = "Le contenu du message est obligatoire")
        String message,
        @NotNull(message = "L'identifiant de l'employé est obligatoire")
        UUID employeeId,
        @NotNull(message = "L'identifiant de la fiche de notation est obligatoire")
        UUID scorecardId
) implements Command<DisputesUseCases, UUID> {
    @Override
    public UUID execute(DisputesUseCases useCase) throws MessagingException, IOException {
        return useCase.createDispute(this);
    }
}
