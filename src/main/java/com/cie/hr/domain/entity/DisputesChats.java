package com.cie.hr.domain.entity;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Getter
public class DisputesChats {
    private final UUID id;
    private final String subject;
    private final String message;
    private final Disputes dispute;
    private final boolean isRejected;
    private final LocalDateTime createdAt;

    private DisputesChats(Builder builder) {
        id = builder.id;
        subject = builder.subject;
        message = builder.message;
        dispute = builder.dispute;
        createdAt = builder.createdAt;
        isRejected = builder.isRejected;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public static final class Builder {
        private UUID id;
        private String subject;
        private String message;
        private Disputes dispute;
        private LocalDateTime createdAt;
        private boolean isRejected;

        private Builder() {
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder subject(String val) {
            subject = val;
            return this;
        }

        public Builder message(String val) {
            message = val;
            return this;
        }

        public Builder dispute(Disputes val) {
            dispute = val;
            return this;
        }

        public Builder createdAt(LocalDateTime val) {
            createdAt = val;
            return this;
        }

        public Builder isRejected(boolean val) {
            isRejected = val;
            return this;
        }

        public DisputesChats build() {
            return new DisputesChats(this);
        }
    }
}
