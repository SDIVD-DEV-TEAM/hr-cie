package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

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
@Table(name = "organization_types", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"}, name = "uc_code")})
public class OrganizationTypeEntity extends AbstractEntity {
    @Column
    private String name;

    @Column(unique = true)
    private String code;

    @Column
    private String gradeCode;
}
