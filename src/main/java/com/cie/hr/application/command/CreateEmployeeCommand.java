package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public record CreateEmployeeCommand(
        String lastname,
        @NotNull(message = "Le nom est obligatoire")
        String firstname,
        @NotNull(message = "L'adresse e-mail est obligatoire")
        @Email(message = "Merci de fournir une adresse e-mail valide")
        String email,
        UUID jobId,
        @NotNull(message = "Le profil est obligatoire")
        UUID profileId,
        String accessLevel,
        String employeeNumber) implements Command<EmployeeUseCases, UUID> {

    @Override
    public UUID execute(EmployeeUseCases useCase) throws MessagingException, IOException {
        return useCase.createEmployee(this);
    }
}
