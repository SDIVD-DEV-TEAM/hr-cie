package com.cie.hr.application.adapter;

import com.cie.hr.application.command.UpdateScorecardExpertTemplateCommand;
import com.cie.hr.application.command.UpdateScorecardManagerTemplateCommand;
import com.cie.hr.domain.entity.ScorecardTemplate;
import com.cie.hr.domain.port.ScorecardTemplateRepositoryPort;
import com.cie.hr.domain.usecase.ScorecardTemplateUseCases;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
@Component
public class ScorecardTemplateUseCasesAdapter implements ScorecardTemplateUseCases {

    private final ScorecardTemplateRepositoryPort scorecardTemplateRepositoryPort;

    public ScorecardTemplateUseCasesAdapter(ScorecardTemplateRepositoryPort scorecardTemplateRepositoryPort) {
        this.scorecardTemplateRepositoryPort = scorecardTemplateRepositoryPort;
    }

    @Override
    public UUID updateScorecardManagerTemplate(UpdateScorecardManagerTemplateCommand command) {

        var scorecardManagerTemplate = ScorecardTemplate.builder()
                .id(command.data().id())
                .title(command.data().title())
                .formManager(command.data().form())
                .deleted(false)
                .active(true).toBuild();
        scorecardTemplateRepositoryPort.updateAndSave(scorecardManagerTemplate);
        return scorecardManagerTemplate.getId();
    }

    @Override
    public UUID updateScorecardExpertTemplate(UpdateScorecardExpertTemplateCommand command) {
        var scorecardManagerTemplate = ScorecardTemplate.builder()
                .id(command.data().id())
                .title(command.data().title())
                .formExpert(command.data().form())
                .deleted(false)
                .active(true).toBuild();
        scorecardTemplateRepositoryPort.updateAndSave(scorecardManagerTemplate);
        return scorecardManagerTemplate.getId();
    }
}
