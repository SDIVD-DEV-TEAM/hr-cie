package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.GradeUseCases;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public record UpdateGradeCommand(UUID id, String name) implements Command<GradeUseCases, UUID> {
    @Override
    public UUID execute(GradeUseCases useCase) {
        return null;
    }
}
