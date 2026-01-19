package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.ScoreNoteDistribution;
import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import com.cie.hr.infrastructure.entity.ScoreEntity;
import com.cie.hr.infrastructure.entity.ScoreNoteDistributionEntity;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public class ScoreNoteDistributionMapper {

    public static ScoreNoteDistributionEntity toEntity(ScoreNoteDistribution scoreNoteDistribution) {
        if (scoreNoteDistribution == null) {
            return null;
        }

        return ScoreNoteDistributionEntity.builder()
                .id(scoreNoteDistribution.getId())
                .registeredAt(scoreNoteDistribution.getRegisteredAt())
                .maxInterval(scoreNoteDistribution.getMaxScore())
                .minInterval(scoreNoteDistribution.getMinScore())
                .noteDistribution(NoteDistributionMapper.toEntity(scoreNoteDistribution.getNoteDistribution()))
                .score(ScoreMapper.toEntity(scoreNoteDistribution.getScore()))
                .build();
    }

    public static ScoreNoteDistribution toDomain(ScoreNoteDistributionEntity scoreNoteDistribution) {
        if (scoreNoteDistribution == null) {
            return null;
        }

        return ScoreNoteDistribution.newBuilder()
                .id(scoreNoteDistribution.getId())
                .maxScore(scoreNoteDistribution.getMaxInterval())
                .minScore(scoreNoteDistribution.getMinInterval())
                .noteDistribution(NoteDistributionMapper.toDomain(scoreNoteDistribution.getNoteDistribution()))
                .score(ScoreMapper.toDomain(scoreNoteDistribution.getScore()))
                .registeredAt(scoreNoteDistribution.getRegisteredAt())
                .build();
    }

    public static void updateAndSave(ScoreNoteDistribution domain, ScoreNoteDistributionEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getId(), entity.getId())) {
            entity.setId(domain.getId());
        }

        if (!Objects.equals(domain.getRegisteredAt(), entity.getRegisteredAt())) {
            entity.setRegisteredAt(domain.getRegisteredAt());
        }

        if (!Objects.equals(domain.getMaxScore(), entity.getMaxInterval())) {
            entity.setMaxInterval(domain.getMaxScore());
        }

        if (!Objects.equals(domain.getMinScore(), entity.getMinInterval())) {
            entity.setMinInterval(domain.getMinScore());
        }

        NoteDistributionEntity noteDistributionEntity = NoteDistributionMapper.toEntity(domain.getNoteDistribution());
        if (noteDistributionEntity != null && (entity.getNoteDistribution() == null ||
                !Objects.equals(entity.getNoteDistribution(), noteDistributionEntity))) {
            entity.setNoteDistribution(noteDistributionEntity);
        }

        ScoreEntity scoreEntity = ScoreMapper.toEntity(domain.getScore());
        if (scoreEntity != null && (entity.getScore() == null ||
                !Objects.equals(entity.getScore(), scoreEntity))) {
            entity.setScore(scoreEntity);
        }
    }
}
