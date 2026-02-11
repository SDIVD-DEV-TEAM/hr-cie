package com.cie.hr.domain.usecase;

import java.util.UUID;

import com.cie.hr.application.command.CloseCampaignCommand;
import com.cie.hr.application.command.CreateCampaignCommand;
import com.cie.hr.application.command.DeleteCampaignCommand;
import com.cie.hr.application.command.OpenCampaignCommand;
import com.cie.hr.application.command.UpdateCampaignCommand;

public interface CampaignUseCases {
    UUID createCampaign(CreateCampaignCommand command);

    UUID updateCampaign(UpdateCampaignCommand command);

    Boolean deleteCampaign(DeleteCampaignCommand command);

    Boolean closeCampaign(CloseCampaignCommand command);

    Boolean openCampaign(OpenCampaignCommand command);

    /**
     * Synchronise les scorecards manquants pour la campagne active.
     * Détecte les employés avec un poste actif mais sans scorecard et les crée.
     * @return Le nombre de scorecards créés
     */
    int syncMissingScorecards();
}
