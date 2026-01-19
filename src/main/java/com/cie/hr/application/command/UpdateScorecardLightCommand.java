package com.cie.hr.application.command;

import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 08/06/2023
 * @project hr-cie
 */
public record UpdateScorecardLightCommand(
        UUID managerId,
        EvaluationScorecardManager scorecardManager,
        EvaluationScorecardExpert scorecardExpert
) {
}
