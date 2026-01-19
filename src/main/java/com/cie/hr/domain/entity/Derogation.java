package com.cie.hr.domain.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Getter
public class Derogation {
    private final UUID id;
    private final EmployeeDomain employee;
    private final EmployeeDomain manager;
    private final Campaign campaign;
    private final LocalDate expiredAt;
    @Setter
    private Boolean deleted;

    public Derogation(Builder builder) {
        id = builder.id;
        employee = builder.employee;
        manager = builder.manager;
        campaign = builder.campaign;
        expiredAt = builder.expiredAt;
        deleted = builder.deleted;
    }

    public static class Builder {
        private UUID id;
        private EmployeeDomain employee;
        private EmployeeDomain manager;
        private Campaign campaign;
        private LocalDate expiredAt;
        private Boolean deleted;

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder employee(EmployeeDomain val) {
            employee = val;
            return this;
        }

        public Builder manager(EmployeeDomain val) {
            manager = val;
            return this;
        }

        public Builder campaign(Campaign val) {
            campaign = val;
            return this;
        }

        public Builder expiredAt(LocalDate val) {
            expiredAt = val;
            return this;
        }

        public Builder deleted(Boolean val) {
            deleted = val;
            return this;
        }

        public Derogation build() {
            return new Derogation(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isDeleted() {
        return deleted;
    }
}
