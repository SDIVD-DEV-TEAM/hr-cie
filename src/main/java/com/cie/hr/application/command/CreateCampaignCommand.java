package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.CampaignUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.Date;
import java.util.UUID;

public record CreateCampaignCommand(
        @NotNull(message = "Le nom de la campagne est obligatoire")
        String name,
        @NotNull(message = "La date de début est obligatoire")
        Date start_date,
        @NotNull(message = "La date de fin est obligatoire")
        Date end_date
) implements Command<CampaignUseCases, UUID> {
    @Override
    public UUID execute(CampaignUseCases useCases) {
        return useCases.createCampaign(this);
    }
}
