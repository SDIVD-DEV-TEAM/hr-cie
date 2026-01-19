package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.valueobject.Mobility;
import com.cie.hr.infrastructure.entity.MobilityEntity;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
public class MobilityMapper {
    public static Mobility toMobilityDomain(MobilityEntity mobility) {
        if (mobility == null) {
            return null;
        }
        return new Mobility(mobility.getId(), mobility.getLabel());
    }

    public static MobilityEntity toMobilityEntity(Mobility mobility) {
        if (mobility == null) {
            return null;
        }
        return new MobilityEntity(mobility.id(), mobility.label());
    }

    public static void updateAndSave(Mobility domain, MobilityEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!domain.label().equals(entity.getLabel())) {
            entity.setLabel(domain.label());
        }
    }
}
