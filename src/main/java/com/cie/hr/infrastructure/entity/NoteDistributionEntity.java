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
@Table(name = "note_distribution", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"}, name = "uc_note_distribution_code")})
public class NoteDistributionEntity {
    @Id
    private UUID id;

    @Column(columnDefinition = "varchar(5)")
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String category;

    @Column(columnDefinition = "varchar(5)")
    private String way;

    @Column(name = "may_exceed")
    private boolean mayExceed;

    @Column(name = "max_when_may_exceed")
    private double maxWhenMayExceed;

    @Column(name = "all_or_nothing")
    private boolean allOrNothing;
}
