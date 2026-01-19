package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.UpdateScorecardExpertTemplateCommand;
import com.cie.hr.application.command.UpdateScorecardManagerTemplateCommand;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public interface ScorecardTemplateUseCases {

    UUID updateScorecardManagerTemplate(UpdateScorecardManagerTemplateCommand command);
    UUID updateScorecardExpertTemplate(UpdateScorecardExpertTemplateCommand command);

}
