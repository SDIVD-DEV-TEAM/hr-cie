package com.cie.hr.domain.entity;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.ProfileRepositoryPort;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Setter
@Getter
public class Profile {
    private UUID id;
    private String name;
    private String code;

    private Profile(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setCode(builder.code);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void checkBusinessRules(ProfileRepositoryPort profileRepositoryPort) {
        // checks if name or code already exists
        boolean result = profileRepositoryPort.searchIfExists(name, code);
        if (result) {
            throw new DomainException("Ce profile existe déjà");
        }
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String code;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder id(UUID val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder code(String val) {
            code = val;
            return this;
        }

        public Profile build() {
            return new Profile(this);
        }
    }
}
