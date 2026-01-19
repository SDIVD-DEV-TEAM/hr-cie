package com.cie.hr.application.command;

import com.cie.hr.domain.usecase.UnitsUseCases;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;
import jakarta.validation.constraints.NotNull;

/**
 * @author Alexis TAMBIE
 * @created 22/01/2025
 * @project hr-cie
 */
public record CreateUnitCommand(
        @NotNull(message = "Unit name is required")
        String name,
        @NotNull(message = "Unit description is required")
        String description
) implements Command<UnitsUseCases, UnitsVM> {
    @Override
    public UnitsVM execute(UnitsUseCases useCases) {
        return useCases.createUnit(this);
    }
}
