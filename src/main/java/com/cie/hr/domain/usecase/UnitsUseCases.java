package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CreateUnitCommand;
import com.cie.hr.application.command.UpdateUnitCommand;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;

/**
 * @author Alexis TAMBIE
 * @created 22/01/2025
 * @project hr-cie
 */
public interface UnitsUseCases {
    UnitsVM createUnit(CreateUnitCommand command);
    UnitsVM updateUnit(UpdateUnitCommand command);
}
