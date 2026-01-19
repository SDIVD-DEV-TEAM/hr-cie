package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.CampaignUseCases;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 17/07/2023
 * @project hr-cie
 */
public record DeleteCampaignCommand(
        @NotNull(message = "L'identifiant de la campagne est obligatoire")
        UUID campaignId
) implements Command<CampaignUseCases, Boolean> {
    @Override
    public Boolean execute(CampaignUseCases useCase) {
        return useCase.deleteCampaign(this);
    }
}
