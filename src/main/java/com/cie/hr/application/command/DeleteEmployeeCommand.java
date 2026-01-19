package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 17/07/2023
 * @project hr-cie
 */
public record DeleteEmployeeCommand(
        @NotNull(message = "L'identifiant de l'employé est obligatoire")
        UUID id
) implements Command<EmployeeUseCases, Boolean>{
    @Override
    public Boolean execute(EmployeeUseCases useCase) throws MessagingException, IOException {
        return useCase.deleteEmployee(this);
    }
}
