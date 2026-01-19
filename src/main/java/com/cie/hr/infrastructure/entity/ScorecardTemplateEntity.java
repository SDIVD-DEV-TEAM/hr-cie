package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "scorecard_templates")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.INTEGER)

public abstract class ScorecardTemplateEntity extends AbstractEntity {
    @Column
    protected String title;

    @Column
    protected boolean active;

    @Column(name = "type", insertable = false, updatable = false)
    protected int type;
}
