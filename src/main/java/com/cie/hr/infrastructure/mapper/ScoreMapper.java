package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Score;
import com.cie.hr.infrastructure.entity.ScoreEntity;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public class ScoreMapper {

    public static ScoreEntity toEntity(Score score) {
        if (score == null) {
            return null;
        }

        return ScoreEntity.builder()
                .id(score.id())
                .score(score.score())
                .build();
    }

    public static Score toDomain(ScoreEntity scoreEntity) {
        if (scoreEntity == null) {
            return null;
        }

        return new Score(scoreEntity.getId(), scoreEntity.getScore());
    }

    public static void updateAndSave(Score domain, ScoreEntity entity) {
        if (entity == null || domain == null) {
            return;
        }
        if (!Objects.equals(domain.id(), entity.getId())) {
            entity.setId(domain.id());
        }
        if (!Objects.equals(domain.score(), entity.getScore())) {
            entity.setScore(domain.score());
        }
    }
}
