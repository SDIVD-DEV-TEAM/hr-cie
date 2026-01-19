package com.cie.hr.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 06/05/2023
 * @project hr-cie
 */
@Entity
@Table(name = "password_store")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class PasswordStoreEntity {
    @Id
    private UUID id;

    @Column
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
