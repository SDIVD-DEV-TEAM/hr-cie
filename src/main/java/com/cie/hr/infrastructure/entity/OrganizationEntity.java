package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

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
@Builder
@Table(name = "organizations", uniqueConstraints = {
        @UniqueConstraint(name = "uc_organization_code", columnNames = {"code"})
})
public class OrganizationEntity extends AbstractEntity {
    @Column
    private String name;

    @Column
    private String code;

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_organization_organization_parent"))
    private OrganizationEntity parent;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false, foreignKey = @ForeignKey(name = "fk_organization_organization_type"))
    private OrganizationTypeEntity type;

    @OneToMany(mappedBy = "organization")
    List<JobEntity> jobs;

    @Column
    private String costCenter;

    @Column
    private String shortCode;

    public JobEntity getChiefJob() {
        if (parent == null || parent.getJobs() == null || parent.getJobs().isEmpty()) {
            return null;
        }

        // Définir l'ordre hiérarchique des grades
        Map<String, Integer> gradeHierarchy = Map.of(
                "DG", 1,
                "DGA", 2,
                "D", 3,
                "DA", 4,
                "SD", 5,
                "DR", 5,
                "AS", 5
        );
        // Trouver le job avec le grade le plus élevé
        return this.parent.jobs.stream()
                .filter(job -> job.getGrade() != null && gradeHierarchy.containsKey(job.getGrade().getCode()))
                .min(Comparator.comparingInt(job -> gradeHierarchy.get(job.getGrade().getCode())))
                .orElse(null); // Retourner null si aucun job valide n'est trouvé
    }

    public String getChiefOrganization() {
        if (this.parent != null && this.parent.type.getCode().equals("1")) {
            return this.parent.shortCode != null ? "%s %s".formatted("Pôle", this.parent.shortCode) : "";
        }

        if (this.parent == null || this.type != null && "1".equals(this.type.getCode())) {
            return this.shortCode != null ? "%s %s".formatted("Pôle", this.shortCode) : "";
        }

        return this.parent.getChiefOrganization();
    }

}
