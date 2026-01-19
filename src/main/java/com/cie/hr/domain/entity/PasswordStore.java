package com.cie.hr.domain.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 06/05/2023
 * @project hr-cie
 */
@Getter
public class PasswordStore {
    private final UUID id;
    private final String lastPassword;
    private final LocalDateTime storageDate;
    private final EmployeeDomain employee;
    private PasswordStore(Builder builder) {
        id = builder.id;
        lastPassword = builder.lastPassword;
        storageDate = builder.storageDate;
        employee = builder.employee;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    static class SortByDate implements Comparator<PasswordStore>, Serializable {
        @Override
        public int compare(PasswordStore a, PasswordStore b) {
            return a.getStorageDate().compareTo(b.getStorageDate());
        }
    }

    public static final class Builder {
        private UUID id;
        private String lastPassword;
        private LocalDateTime storageDate;
        private EmployeeDomain employee;
        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder lastPassword(String val) {
            lastPassword = val;
            return this;
        }

        public Builder storageDate(LocalDateTime val) {
            storageDate = val;
            return this;
        }

        public Builder employee(EmployeeDomain val) {
            employee = val;
            return this;
        }


        public PasswordStore build() {
            return new PasswordStore(this);
        }
    }
}