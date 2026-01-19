package com.cie.hr.application.adapter;


import com.cie.hr.application.command.CreateProfileCommand;
import com.cie.hr.domain.entity.Profile;
import com.cie.hr.domain.port.ProfileRepositoryPort;
import com.cie.hr.domain.usecase.ProfileUseCases;
import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Component
public class ProfileUseCasesAdapter implements ProfileUseCases {

    private final ProfileRepositoryPort profileRepositoryPort;

    public ProfileUseCasesAdapter(ProfileRepositoryPort profileRepositoryPort) {
        this.profileRepositoryPort = profileRepositoryPort;
    }

    @Override
    public UUID createProfile(CreateProfileCommand command) {
        command.checkValidity();
        var profile = Profile.newBuilder()
                .code(command.code())
                .id(Generators.timeBasedEpochGenerator().generate())
                .name(command.name())
                .build();
        profile.checkBusinessRules(profileRepositoryPort);
        this.profileRepositoryPort.save(profile);
        return profile.getId();
    }

    @Override
    public UUID deleteProfile(UUID id) {
        return null;
    }
}
