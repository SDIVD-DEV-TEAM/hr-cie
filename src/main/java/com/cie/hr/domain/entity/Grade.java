package com.cie.hr.domain.entity;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.GradeRepositoryPort;
import com.fasterxml.uuid.Generators;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
public class Grade {
    private final UUID id;
    private final String name;
    private final String code;
    private final String description;
    private final Integer rank;
    private final boolean active;

    public Grade(UUID id, String name, String code, String description, Integer rank, boolean active) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.rank = rank;
        this.active = active;
    }

    public Grade(String name, String code, String description, Integer rank, boolean active) {
        this(Generators.timeBasedEpochGenerator().generate(), name, code, description, rank, active);
    }

    public void checksBusinessRules(GradeRepositoryPort gradeRepositoryPort) {
        // Checks if name is already used
        boolean result = gradeRepositoryPort.checkNameAlreadyExists(name);
        if (result) {
            // levée d'exception
            throw new DomainException("Ce grade existe déjà");
        }

    }
}
