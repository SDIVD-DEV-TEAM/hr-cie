package com.cie.hr.domain.entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Getter
public class ScoreNoteDistribution {
    private final UUID id;
    private final Score score;
    private final NoteDistribution noteDistribution;
    private final double minScore;
    private final double maxScore;
    private final LocalDateTime registeredAt;

    private ScoreNoteDistribution(Builder builder) {
        id = builder.id;
        score = builder.score;
        noteDistribution = builder.noteDistribution;
        minScore = builder.minScore;
        maxScore = builder.maxScore;
        registeredAt = builder.registeredAt;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private UUID id;
        private Score score;
        private NoteDistribution noteDistribution;
        private double minScore;
        private double maxScore;
        private LocalDateTime registeredAt;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder score(Score val) {
            score = val;
            return this;
        }

        public Builder noteDistribution(NoteDistribution val) {
            noteDistribution = val;
            return this;
        }

        public Builder minScore(double val) {
            minScore = val;
            return this;
        }

        public Builder maxScore(double val) {
            maxScore = val;
            return this;
        }

        public Builder registeredAt(LocalDateTime val) {
            registeredAt = val;
            return this;
        }

        public ScoreNoteDistribution build() {
            return new ScoreNoteDistribution(this);
        }
    }
}
