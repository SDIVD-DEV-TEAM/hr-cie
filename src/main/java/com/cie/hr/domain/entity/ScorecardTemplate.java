package com.cie.hr.domain.entity;

import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
public class ScorecardTemplate {
    private final UUID id;
    private final String title;
    private final ScorecardForManagerForm formManager;
    private final ScorecardForExpert formExpert;
    private final Boolean active;
    private final Boolean deleted;

    private ScorecardTemplate(Builder builder) {
        id = builder.id;
        title = builder.title;
        formManager = builder.formManager;
        formExpert = builder.formExpert;
        active = builder.active;
        deleted = builder.deleted;
    }

    public static final class Builder {
        private UUID id;
        private String title;
        private ScorecardForManagerForm formManager;
        private ScorecardForExpert formExpert;
        private Boolean active;
        private Boolean deleted;

        public Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder title(String val) {
            title = val;
            return this;
        }

        public Builder formManager(ScorecardForManagerForm val) {
            formManager = val;
            return this;
        }

        public Builder formExpert(ScorecardForExpert val) {
            formExpert = val;
            return this;
        }

        public Builder active(Boolean val) {
            active = val;
            return this;
        }

        public Builder deleted(Boolean val) {
            deleted = val;
            return this;
        }

        public ScorecardTemplate toBuild() {
            return new ScorecardTemplate(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
