package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.*;

import java.util.UUID;

public interface CampaignUseCases {
    UUID createCampaign(CreateCampaignCommand command);

    UUID updateCampaign(UpdateCampaignCommand command);

    Boolean deleteCampaign(DeleteCampaignCommand command);

    Boolean closeCampaign(CloseCampaignCommand command);

    Boolean openCampaign(OpenCampaignCommand command);
}
