package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardExpert;
import com.cie.hr.infrastructure.valueobject.EvaluationScorecardManager;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
@Table(name = "scorecards")
public class ScorecardEntity extends AbstractEntity {
    @Type(JsonType.class)
    @Column(name = "manager_template", columnDefinition = "jsonb")
    private EvaluationScorecardManager managerTemplate;

    @Type(JsonType.class)
    @Column(name = "expert_template", columnDefinition = "jsonb")
    private EvaluationScorecardExpert expertTemplate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private CampaignEntity campaign;

    @ManyToOne
    @JoinColumn(name = "manager_id", foreignKey = @ForeignKey(name = "fk_employees_scorecards_evaluations"))
    private EmployeeEntity manager;

    @ManyToOne
    @JoinColumn(name = "assessed_id", foreignKey = @ForeignKey(name = "fk_employees_scorecards_performance"))
    private EmployeeEntity assessed;

    @ManyToOne
    @JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "fk_scorecards_status"))
    private StatusEntity status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "title", column = @Column(name = "job_title")),
            @AttributeOverride(name = "code", column = @Column(name = "job_code")),
            @AttributeOverride(name = "organization", column = @Column(name = "job_organization")),
            @AttributeOverride(name = "grade", column = @Column(name = "job_grade")),
            @AttributeOverride(name = "organization_type", column = @Column(name = "job_organization_type"))
    })
    private JobEmbeddedEntity job;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "title", column = @Column(name = "manager_job_title")),
            @AttributeOverride(name = "code", column = @Column(name = "manager_job_code")),
            @AttributeOverride(name = "organization", column = @Column(name = "manager_job_organization")),
            @AttributeOverride(name = "grade", column = @Column(name = "manager_job_grade")),
            @AttributeOverride(name = "organization_type", column = @Column(name = "manager_job_organization_type"))
    })
    private JobEmbeddedEntity managerJob;

    @Column
    private LocalDateTime evaluated_at;

    @Column(name = "automatic_closed")
    private boolean automaticClosed;

    @OneToMany(mappedBy = "scorecard")
    @Cascade(CascadeType.ALL)
    List<DisputesEntity> disputes;
}
