package com.cie.hr.application.command;

import com.cie.hr.domain.entity.Organization;
import com.cie.hr.domain.usecase.OrganizationUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrganizationCommand(
        @NotNull(message = "Le nom de l'organisation est obligatoire")
        String name,
        @NotNull(message = "Le code de l'organisation est obligatoire")
        String code,
        UUID parentId,
        @NotNull(message = "Le type de l'organisation est obligatoire")
        UUID typeId,
        @NotNull(message = "Le centre de coût est obligatoire")
        String costCenter,
        String shortCode
) implements Command<OrganizationUseCases, Organization> {
    public Organization execute(OrganizationUseCases useCases) {
        return useCases.createOrganization(this);
    }
}
