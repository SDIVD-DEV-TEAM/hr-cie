package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.validator.constraints.Length;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "grades")
@Builder
public class GradeEntity extends AbstractEntity {
    @Column
    private String name;

    @Column
    private String code;

    @Column
    @Length(max = 150)
    private String description;

    @Column
    private Integer rank;

    @Column
    private boolean active;
}
