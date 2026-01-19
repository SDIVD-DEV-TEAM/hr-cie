package com.cie.hr.domain.entity;

import com.fasterxml.uuid.Generators;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Getter
public class Disputes {
    private final UUID id;
    private final List<DisputesChats> messages;
    private final EmployeeDomain employee;
    private final ScorecardDomain scorecard;

    public void addNewMessageToAStore(String subject, String message, boolean isRejected) {
        this.messages.add(DisputesChats.newBuilder()
                .message(message)
                .subject(subject)
                .isRejected(isRejected)
                .id(Generators.timeBasedEpochGenerator().generate())
                .createdAt(LocalDateTime.now())
                .build());
    }

    private Disputes(Builder builder) {
        id = builder.id;
        messages = builder.messages;
        employee = builder.employee;
        scorecard = builder.scorecard;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private UUID id;
        private List<DisputesChats> messages;
        private EmployeeDomain employee;
        private ScorecardDomain scorecard;

        private Builder() {
            messages = new ArrayList<>();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder messages(List<DisputesChats> val) {
            messages = val;
            return this;
        }

        public Builder employee(EmployeeDomain val) {
            employee = val;
            return this;
        }

        public Builder scorecard(ScorecardDomain val) {
            scorecard = val;
            return this;
        }

        public Disputes build() {
            return new Disputes(this);
        }
    }
}
