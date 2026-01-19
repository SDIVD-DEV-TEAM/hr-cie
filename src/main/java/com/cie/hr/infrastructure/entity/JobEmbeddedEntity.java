package com.cie.hr.infrastructure.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

/**
 * @author Alexis TAMBIE
 * @created 20/07/2023
 * @project hr-cie
 */
@Embeddable
@Getter
public class JobEmbeddedEntity {
    private String title;
    private String code;
    private String organization;
    private String grade;
    private String organization_type;

    public JobEmbeddedEntity() {
    }

    public JobEmbeddedEntity(String title, String code, String organization, String grade, String organization_type) {
        this.title = title;
        this.code = code;
        this.organization = organization;
        this.grade = grade;
        this.organization_type = organization_type;
    }
}
