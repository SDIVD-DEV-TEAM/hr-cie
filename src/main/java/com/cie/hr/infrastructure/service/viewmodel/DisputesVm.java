package com.cie.hr.infrastructure.service.viewmodel;

import java.util.List;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/06/2023
 * @project hr-cie
 */
public record DisputesVm(
        UUID id,
        ScorecardVm scorecard,
        EmployeeVm employee,
        List<DisputesChatsVm> messages
) {
}
