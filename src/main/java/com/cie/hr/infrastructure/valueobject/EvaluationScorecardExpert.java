package com.cie.hr.infrastructure.valueobject;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/05/2023
 * @project hr-cie
 */
public record EvaluationScorecardExpert(
        UUID campaignId,
        Double note,
        String status,
        ScorecardForExpert forms
) {
    public EvaluationScorecardExpert(UUID campaignId, Double note, String status, ScorecardForExpert forms) {
        this.campaignId = campaignId;
        this.note = calc(forms);
        this.status = status;
        this.forms = forms;
    }

    static double calc(ScorecardForExpert forms) {
        return 4 * (forms.sectionA().note() * forms.sectionA().coefficient()) + 4 * (forms.sectionB().note() * forms.sectionB().coefficient());
    }

}
