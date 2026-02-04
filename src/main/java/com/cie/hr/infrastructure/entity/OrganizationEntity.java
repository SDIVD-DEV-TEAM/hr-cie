package com.cie.hr.infrastructure.entity;

import java.util.Comparator;
import java.util.List;

import com.cie.hr.common.adapter.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        // Chercher le chief job dans CETTE organisation, pas dans le parent
        if (this.jobs == null || this.jobs.isEmpty()) {
            return null;
        }

        // Utiliser le champ 'rank' du grade pour déterminer le chef
        // Plus le rank est bas, plus le grade est élevé (DG=0, DGA=2, DC=3, etc.)
        return this.jobs.stream()
                .filter(job -> job.getGrade() != null && !job.isDeleted())
                .min(Comparator.comparingInt(job -> job.getGrade().getRank()))
                .orElse(null);
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
