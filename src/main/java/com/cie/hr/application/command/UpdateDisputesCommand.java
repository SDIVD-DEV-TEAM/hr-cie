package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.DisputesUseCases;
import jakarta.mail.MessagingException;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/06/2023
 * @project hr-cie
 */
public record UpdateDisputesCommand(
        UUID disputeId,
        String subject,
        String message,
        boolean isRejected
) implements Command<DisputesUseCases, UUID> {
    @Override
    public UUID execute(DisputesUseCases useCase) throws MessagingException, IOException {
        return useCase.updateDispute(this);
    }
}
