package com.cie.hr.domain.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class Status {
    private UUID id;
    private String name;
    private String code;
    private Boolean deleted;

    private Status(Builder builder) {
        setId(builder.id);
        setName(builder.name);
        setCode(builder.code);
        setDeleted(builder.deleted);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private String code;
        private Boolean deleted;

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

        public Builder deleted(Boolean val) {
            deleted = val;
            return this;
        }

        public Status build() {
            return new Status(this);
        }
    }
}
