package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.JobUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/05/2023
 * @project hr-cie
 */
public record AssignJobToEmployeeCommand(
        @NotNull(message = "Le poste est obligatoire")
        UUID jobId,
        UUID employeeId
) implements Command<JobUseCases, Boolean> {
    @Override
    public Boolean execute(JobUseCases useCase) {
        return useCase.assignJobToEmployee(this);
    }
}
