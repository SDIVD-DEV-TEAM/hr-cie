package com.cie.hr.domain.entity;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.OrganizationRepositoryPort;
import com.cie.hr.domain.port.OrganizationTypeRepositoryPort;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
public class Organization {
    protected UUID id;
    protected String name;
    protected String code;
    @Nullable
    protected Organization parent;
    protected OrganizationType type;
    protected String costCenter;
    protected String shortCode;

    public Organization(UUID id, String name, String code, @Nullable Organization parentId, OrganizationType typeId, String costCenter, String shortCode) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parent = parentId;
        this.type = typeId;
        this.costCenter = costCenter;
        this.shortCode = shortCode;
    }

    public void checksBusinessRules(OrganizationRepositoryPort organizationRepositoryPort, OrganizationTypeRepositoryPort organizationTypeRepositoryPort) {
        // Checks if name is already used
        var result = organizationRepositoryPort.checkNameOrCodeAlreadyExists(name, code);
        if (result) {
            throw new DomainException("Nom : " + name + " ou Code : " + code + " existe déjà");
        }

        // Checks if parent is the right one
        if (parent != null) {
            boolean resultChecking = organizationTypeRepositoryPort.checkChildAndParentOrganizationType(type.getId(), parent.getId()).orElse(false);
            if (!resultChecking) {
                throw new DomainException("Le Parent :" + parent.name + " n'est pas accepté pour ce type d'organisation " + type.name);
            }
        }
    }

    @Nullable
    public Organization getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", parent=" + parent +
                ", type=" + type +
                ", costCenter='" + costCenter + '\'' +
                ", shortCode='" + shortCode + '\'' +
                '}';
    }
}
