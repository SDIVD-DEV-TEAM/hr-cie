package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 13/02/2025
 * @project hr-cie
 */
public record ReactivateUserCommand(
        UUID employeeId
) implements Command<EmployeeUseCases, Boolean> {

    @Override
    public Boolean execute(EmployeeUseCases useCase) {
        return useCase.reactivateEmployee(this);
    }
}
