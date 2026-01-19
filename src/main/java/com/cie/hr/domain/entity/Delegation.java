package com.cie.hr.domain.entity;

import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Getter
public class Delegation {
    private final UUID id;
    private final EmployeeDomain employee;
    private final EmployeeDomain giver;
    private final EmployeeDomain receiver;
    private final Campaign campaign;
    @Setter
    private Boolean deleted;
    private final String reason;


    private Delegation(Builder builder) {
        id = builder.id == null ? Generators.timeBasedEpochGenerator().generate() : builder.id;
        employee = builder.employee;
        receiver = builder.receiver;
        giver = builder.giver;
        campaign = builder.campaign;
        deleted = builder.deleted;
        reason = builder.reason;
    }

    public static class Builder {
        private UUID id;
        private EmployeeDomain employee;
        private EmployeeDomain giver;
        private EmployeeDomain receiver;
        private Campaign campaign;
        private Boolean deleted;
        private String reason;
        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder employee(EmployeeDomain val) {
            employee = val;
            return this;
        }

        public Builder giver(EmployeeDomain val) {
            giver = val;
            return this;
        }

        public Builder receiver(EmployeeDomain val) {
            receiver = val;
            return this;
        }

        public Builder campaign(Campaign val) {
            campaign = val;
            return this;
        }

        public Builder deleted(Boolean val) {
            deleted = val;
            return this;
        }

        public Builder reason(String val) {
            reason = val;
            return this;
        }
        public Delegation build() {
            return new Delegation(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
