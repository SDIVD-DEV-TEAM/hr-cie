package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "units", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "uc_units_name")})
public class UnitsEntity extends AbstractEntity {
    @Column
    private String name;

    @Column
    private String description;

    public UnitsEntity(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = Generators.timeBasedEpochGenerator().generate();
    }
}
