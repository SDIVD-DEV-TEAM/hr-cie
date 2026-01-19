package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.CampaignUseCases;

import java.util.Date;
import java.util.UUID;

public record UpdateCampaignCommand(UUID id, String name, Date start_date, Date end_date) implements Command<CampaignUseCases,UUID>{
    @Override
    public UUID execute(CampaignUseCases useCase) {
        return useCase.updateCampaign(this);
    }
}
