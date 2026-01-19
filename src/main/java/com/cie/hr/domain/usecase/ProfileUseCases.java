package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CreateProfileCommand;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public interface ProfileUseCases {
    UUID createProfile(CreateProfileCommand command);
    UUID deleteProfile(UUID id);
}
