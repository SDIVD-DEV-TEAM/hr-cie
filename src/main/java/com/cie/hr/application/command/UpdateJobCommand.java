package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.JobUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 09/05/2023
 * @project hr
 */
public record UpdateJobCommand(
        @NotNull
        UUID jobId,
        CreateJobCommand job
) implements Command<JobUseCases, UUID> {
    @Override
    public UUID execute(JobUseCases useCase) {
        return useCase.updateJob(this);
    }
}
