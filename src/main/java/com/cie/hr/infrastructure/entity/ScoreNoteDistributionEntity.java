package com.cie.hr.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@Table(name = "score_note_distribution")
@AllArgsConstructor
@NoArgsConstructor
public class ScoreNoteDistributionEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "score_id", foreignKey = @ForeignKey(name = "fk_score_note_distribution_score"))
    private ScoreEntity score;

    @ManyToOne
    @JoinColumn(name = "note_distribution_id", foreignKey = @ForeignKey(name = "fk_score_note_distribution_note"))
    private NoteDistributionEntity noteDistribution;

    @Column
    private double minInterval;

    @Column
    private double maxInterval;

    @Column
    private LocalDateTime registeredAt;
}