package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.JobUseCases;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
public record CreateJobCommand(
        String title,
        String code,
        UUID gradeId,
        UUID employeeId,
        UUID organisationId
) implements Command<JobUseCases, UUID> {
    @Override
    public UUID execute(JobUseCases useCase) {
        return useCase.createJob(this);
    }
}
