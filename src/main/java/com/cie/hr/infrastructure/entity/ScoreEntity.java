package com.cie.hr.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "score", uniqueConstraints = {@UniqueConstraint(columnNames = {"score"}, name = "uc_score_score")})
public class ScoreEntity {
    @Id
    private UUID id;

    @Column
    private float score;
}
