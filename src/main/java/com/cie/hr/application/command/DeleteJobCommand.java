package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.JobUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 09/05/2023
 * @project hr
 */
public record DeleteJobCommand(
        @NotNull(message = "L'identifiant du poste est obligatoire")
        UUID id
) implements Command<JobUseCases, Boolean> {
    @Override
    public Boolean execute(JobUseCases useCase) {
        return useCase.deleteJob(this);
    }
}
