package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import com.cie.hr.infrastructure.valueobject.FormSpecialSection;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import java.util.List;
import java.util.UUID;

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
@Table(name = "jobs", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"employee_id"}, name = "uc_jobs_employee_id"),
        @UniqueConstraint(columnNames = {"code"}, name = "uc_jobs_code")
})
@Builder
public class JobEntity extends AbstractEntity {
    @Column
    private String title;

    @Column
    @Length(max = 30)
    private String code;

    @Type(JsonType.class)
    @Column(name = "job_template", columnDefinition = "jsonb")
    private FormSpecialSection jobTemplate;

    @ManyToOne
    @JoinColumn(name = "grade_id", foreignKey = @ForeignKey(name = "fk_jobs_grades"))
    private GradeEntity grade;

    @ManyToOne
    @JoinColumn(name = "organization_id", foreignKey = @ForeignKey(name = "fk_jobs_organization"))
    private OrganizationEntity organization;

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_jobs_parents"))
    private JobEntity parent;

    @OneToOne
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "fk_jobs_employees"))
    private EmployeeEntity employee;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "parent")
    @OrderBy("title")
    private List<JobEntity> children;

    public void setId(UUID id) {
        this.id = id;
    }
}
