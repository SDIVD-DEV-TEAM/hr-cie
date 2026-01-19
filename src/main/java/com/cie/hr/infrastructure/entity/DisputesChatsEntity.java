package com.cie.hr.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "disputes_chat")
public class DisputesChatsEntity {
    @Id
    private UUID id;

    @Column
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column
    private boolean isRejected;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
