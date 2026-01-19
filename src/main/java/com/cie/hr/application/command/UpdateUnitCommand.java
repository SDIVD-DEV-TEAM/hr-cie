package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.UnitsUseCases;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/01/2025
 * @project hr-cie
 */
public record UpdateUnitCommand(
        UUID id,
        CreateUnitCommand unitCommand
) implements Command<UnitsUseCases, UnitsVM> {
    @Override
    public UnitsVM execute(UnitsUseCases useCases) {
        return useCases.updateUnit(this);
    }
}
