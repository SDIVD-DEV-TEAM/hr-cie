package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.valueobject.Formation;
import com.cie.hr.infrastructure.entity.FormationEntity;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
public class FormationMapper {
    public static Formation toFormationDomain(FormationEntity formation) {
        if (formation == null) {
            return null;
        }
        return new Formation(formation.getId(), formation.getLabel());
    }

    public static FormationEntity toFormationEntity(Formation formation) {
        if (formation == null) {
            return null;
        }
        return new FormationEntity(formation.id(), formation.label());
    }

    public static void updateAndSave(Formation domain, FormationEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!domain.label().equals(entity.getLabel())) {
            entity.setLabel(domain.label());
        }
    }
}
