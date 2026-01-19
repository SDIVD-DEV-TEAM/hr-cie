package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Units;
import com.cie.hr.infrastructure.entity.UnitsEntity;
import com.cie.hr.infrastructure.service.viewmodel.UnitsVM;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
public class UnitsMapper {
    public static Units toUnitsDomain(UnitsEntity units) {
        if (units == null) {
            return null;
        }
        return new Units(units.getId(), units.getName(), units.getDescription());
    }

    public static UnitsEntity toUnitsEntity(Units units) {
        if (units == null) {
            return null;
        }
        return new UnitsEntity(units.getName(), units.getDescription());
    }

    public static UnitsVM toUnitsVM(UnitsEntity units) {
        if (units == null) {
            return null;
        }
        return new UnitsVM(units.getId(), units.getName(), units.getDescription());
    }

    public static UnitsVM toUnitsVM(Units units) {
        if (units == null) {
            return null;
        }
        return new UnitsVM(units.getId(), units.getName(), units.getDescription());
    }

    public static void updateAndSave(Units domain, UnitsEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        if (!Objects.equals(domain.getDescription(), entity.getDescription())) {
            entity.setDescription(domain.getDescription());
        }
    }
}
