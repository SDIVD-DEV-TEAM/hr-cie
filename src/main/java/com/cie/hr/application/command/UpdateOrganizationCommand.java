package com.cie.hr.application.command;

import com.cie.hr.domain.entity.Organization;
import com.cie.hr.domain.usecase.OrganizationUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateOrganizationCommand(
        @NotNull(message = "Le nom de l'organisation est obligatoire")
        UUID id,
        CreateOrganizationCommand createOrganizationCommand
) implements Command<OrganizationUseCases, Organization> {
    public Organization execute(OrganizationUseCases useCases) {
        return useCases.updateOrganization(this);
    }
}
