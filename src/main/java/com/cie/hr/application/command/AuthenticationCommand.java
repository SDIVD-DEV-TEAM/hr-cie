package com.cie.hr.application.command;

import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public record AuthenticationCommand(
        @NotNull(message = "L'adresse e-mail est obligatoire")
        @Email(message = "Merci de renseigner une adresse e-mail valide")
        String email,
        @NotNull(message = "Le mot de passe est obligatoire")
        String password
) implements Command<EmployeeUseCases, EmployeeDomain> {
    @Override
    public EmployeeDomain execute(EmployeeUseCases useCase) {
        return useCase.authentication(this);
    }
}
