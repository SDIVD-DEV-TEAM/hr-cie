package com.cie.hr.infrastructure.valueobject;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/05/2023
 * @project hr-cie
 */
public record EvaluationScorecardManager(
        UUID campaignId,
        Double note,
        String status,
        ScorecardForManagerForm forms
) {

    public EvaluationScorecardManager(UUID campaignId, Double note, String status, ScorecardForManagerForm forms) {
        this.campaignId = campaignId;
        this.note = calc(forms);
        this.status = status;
        this.forms = forms;
    }

    static double calc(ScorecardForManagerForm forms) {
        return 4 * (forms.sectionA().note() * forms.sectionA().coefficient()) + 4 * (forms.sectionB().note() * forms.sectionB().coefficient()) + 4 * (forms.sectionC().note() * forms.sectionC().coefficient()) + 4 * (forms.sectionD().note() * forms.sectionD().coefficient());
    }
}
