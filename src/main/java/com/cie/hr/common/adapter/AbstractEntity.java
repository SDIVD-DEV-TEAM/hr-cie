package com.cie.hr.common.adapter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 06/05/2023
 * @project hr-cie
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {
    @Id
    protected UUID id;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @JsonIgnore
    private Instant created;

    @LastModifiedDate
    @Column(name = "last_modified")
    @JsonIgnore
    private Instant lastModified;

    @Column(name = "created_by")
    @CreatedBy
    private String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    private String modifiedBy;

    @Column(name = "deleted")
    @ColumnDefault("false")
    private boolean deleted;

    @PrePersist
    protected void prePersist() {
        setCreated(Instant.now(Clock.systemUTC()));
    }

    @PostUpdate
    protected void preUpdate() {
        setLastModified(Instant.now(Clock.systemUTC()));
    }
}
