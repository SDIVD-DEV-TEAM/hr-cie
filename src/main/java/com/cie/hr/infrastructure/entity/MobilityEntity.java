package com.cie.hr.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 03/07/2023
 * @project hr-cie
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "mobility")
public class MobilityEntity {
    @Id
    private UUID id;

    @Column
    private String label;
}
