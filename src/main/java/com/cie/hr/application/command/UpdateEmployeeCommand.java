package com.cie.hr.application.command;

import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.usecase.EmployeeUseCases;
import jakarta.validation.constraints.Email;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public record UpdateEmployeeCommand(
        UUID employeeId,
        String lastname,
        String firstname,
        String employeeNumber,
        UUID profileId,
        @Email(message = "Merci de renseigner un mail valide")
        String email,
        String accessLevel
) implements Command<EmployeeUseCases, EmployeeDomain> {
    @Override
    public EmployeeDomain execute(EmployeeUseCases useCase) {
        return useCase.updateEmployee(this);
    }
}
