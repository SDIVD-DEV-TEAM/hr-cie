package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
public record ResetPasswordCommand(
        @NotNull(message = "Le token est obligatoire!")
        String token,
        @NotBlank(message = "Le mot de passe à modifier est obligatoire!")
        String newPassword,
        @NotBlank(message = "L'adresse e-mail est obligatoire!")
        @Email
        String email
) implements Command<EmployeeUseCases, UUID> {
    @Override
    public UUID execute(EmployeeUseCases useCase) throws MessagingException, IOException {
        return useCase.resetPassword(this);
    }
}
