package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.ProfileUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public record CreateProfileCommand(@NotNull String name, @NotNull String code) implements Command<ProfileUseCases, UUID> {
    @Override
    public UUID execute(ProfileUseCases useCases) {
       return useCases.createProfile(this);
    }
}
