package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public record DelegateNotationCommand(
        @NotNull(message = "Le délégataire est obligatoire")
        UUID employeeId,
        @NotNull(message = "Le destinataire est obligatoire")
        UUID receiverId,
        @NotNull(message = "La campagne est obligatoire")
        UUID campaignId,
        @NotNull(message = "La raison est obligatoire")
        String reason
) implements Command<EmployeeUseCases, Boolean> {
    @Override
    public Boolean execute(EmployeeUseCases useCase) {
        return useCase.delegateNotation(this);
    }
}
