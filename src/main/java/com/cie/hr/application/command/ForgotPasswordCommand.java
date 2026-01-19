package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
public record ForgotPasswordCommand(
        @Email(message = "Merci de renseigner une adresse e-mail valide")
        @NotNull(message = "L'adresse e-mail de l'employé est obligatoire")
        String email
) implements Command<EmployeeUseCases, UUID> {
    @Override
    public UUID execute(EmployeeUseCases useCase) throws MessagingException, IOException {
        return useCase.initChangePassword(this);
    }
}
