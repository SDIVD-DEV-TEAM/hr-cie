package com.cie.hr.domain.entity;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.UnitsRepositoryPort;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@Setter
@Getter
public class Units {
    private UUID id;
    private String name;
    private String description;

    public Units(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void checksBusinessRules(UnitsRepositoryPort unitsRepositoryPort) {
        // Checks if name is already used
        boolean result = unitsRepositoryPort.checkNameAlreadyExists(name);
        if (result) {
             throw new DomainException("Cette unité existe déjà");
        }
    }

}
