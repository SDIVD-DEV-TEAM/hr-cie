package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.JobUseCases;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 30/09/2024
 * @project hr-cie
 */
public record UpdateJobScorecard(
        @NotNull
        MultipartFile file,
        @NotNull
        UUID jobId
) implements Command<JobUseCases, UUID> {
    @Override
    public UUID execute(JobUseCases useCase) {
        return useCase.updateJobScorecard(this);
    }
}
