package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.GradeUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public record CreateGradeCommand(
        @NotNull String name,
        @NotNull String code,
        String description,
        Integer rank,
        boolean active) implements Command<GradeUseCases, UUID> {
    @Override
    public UUID execute(GradeUseCases useCases) {
        return useCases.createGrade(this);
    }
}
