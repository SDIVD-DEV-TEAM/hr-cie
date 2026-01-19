package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.valueobject.ScoreRange;
import com.cie.hr.infrastructure.entity.ScoreRangeEntity;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
public class ScoreRangeMapper {
    public static ScoreRange toScoreRangeDomain(ScoreRangeEntity scoreRange) {
        if (scoreRange == null) {
            return null;
        }
        return new ScoreRange(scoreRange.getId(), scoreRange.getLabel(), scoreRange.getMaxRange(), scoreRange.getMinRange());
    }

    public static ScoreRangeEntity toScoreRangeEntity(ScoreRange scoreRange) {
        if (scoreRange == null) {
            return null;
        }
        return new ScoreRangeEntity(scoreRange.id(), scoreRange.label(), scoreRange.max_range(), scoreRange.min_range());
    }

    public static void updateAndSave(ScoreRange domain, ScoreRangeEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.label(), entity.getLabel())) {
            entity.setLabel(domain.label());
        }

        if (!Objects.equals(domain.max_range(), entity.getMaxRange())) {
            entity.setMaxRange(domain.max_range());
        }

        if (!Objects.equals(domain.min_range(), entity.getMinRange())) {
            entity.setMinRange(domain.min_range());
        }
    }
}
