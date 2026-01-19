package com.cie.hr.domain.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Getter
public class NoteDistribution {
    private final UUID id;
    private final String code;
    private final String category;
    private final String description;
    private final String way; //Up or down
    private final boolean mayExceed;
    private final double maxWhenMayExceed;
    private final boolean allOrNothing;

    public double isMaxWhenMayExceed() {
        return maxWhenMayExceed;
    }

    private NoteDistribution(Builder builder) {
        id = builder.id;
        code = builder.code;
        category = builder.category;
        description = builder.description;
        way = builder.way;
        mayExceed = builder.mayExceed;
        maxWhenMayExceed = builder.maxWhenMayExceed;
        allOrNothing = builder.allOrNothing;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private UUID id;
        private String code;
        private String category;
        private String description;
        private String way;
        private boolean mayExceed;
        private double maxWhenMayExceed;
        private boolean allOrNothing;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Builder category(String val) {
            category = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder way(String val) {
            way = val;
            return this;
        }

        public Builder mayExceed(boolean val) {
            mayExceed = val;
            return this;
        }

        public Builder maxWhenMayExceed(double val) {
            maxWhenMayExceed = val;
            return this;
        }

        public Builder allOrNothing(boolean val) {
            allOrNothing = val;
            return this;
        }

        public NoteDistribution build() {
            return new NoteDistribution(this);
        }
    }
}
