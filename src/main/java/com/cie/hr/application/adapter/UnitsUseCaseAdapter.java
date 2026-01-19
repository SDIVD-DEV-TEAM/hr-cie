package com.cie.hr.application.adapter;

import com.cie.hr.application.command.CreateUnitCommand;
import com.cie.hr.application.command.UpdateUnitCommand;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Units;
import com.cie.hr.domain.port.UnitsRepositoryPort;
import com.cie.hr.domain.usecase.UnitsUseCases;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;
import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 22/01/2025
 * @project hr-cie
 */
@Component
public class UnitsUseCaseAdapter implements UnitsUseCases {

    private final UnitsRepositoryPort unitsRepositoryPort;

    public UnitsUseCaseAdapter(UnitsRepositoryPort unitsRepositoryPort) {
        this.unitsRepositoryPort = unitsRepositoryPort;
    }

    @Override
    public UnitsVM createUnit(CreateUnitCommand command) {
        command.checkValidity();
        String name = command.name().strip();
        String description = command.description().strip();
        Units units = new Units(Generators.timeBasedEpochGenerator().generate(), name, description);
        units.checksBusinessRules(unitsRepositoryPort);
        unitsRepositoryPort.save(units);
        return new UnitsVM(units.getId(), units.getName(), units.getDescription());
    }

    @Override
    public UnitsVM updateUnit(UpdateUnitCommand command) {
        command.checkValidity();
        // Get the unit
        Optional<Units> units = unitsRepositoryPort.findById(command.id());
        if (units.isEmpty()) {
            throw new ApplicationException("Unit not found");
        }

        Units currentUnit = units.get();
        String name = command.unitCommand().name().strip();
        String description = command.unitCommand().description().strip();

        if (!Objects.equals(currentUnit.getName(), name)) {
            boolean checkName = unitsRepositoryPort.checkNameAlreadyExists(name);

            if (checkName) {
                throw new ApplicationException("Unit name already exists");
            }
        }

        currentUnit.setName(name);
        currentUnit.setDescription(description);
        unitsRepositoryPort.updateAndSave(currentUnit);

        return new UnitsVM(currentUnit.getId(), currentUnit.getName(), currentUnit.getDescription());
    }
}
