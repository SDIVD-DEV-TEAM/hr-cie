package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.EmployeeUseCases;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 21/09/2023
 * @project hr-cie
 */
public record RemoveDelegationCommand(UUID campaignId, UUID employeeId, boolean remove) implements Command<EmployeeUseCases, Boolean>{
    @Override
    public Boolean execute(EmployeeUseCases useCase) {
        return useCase.removeDelegation(this);
    }
}
