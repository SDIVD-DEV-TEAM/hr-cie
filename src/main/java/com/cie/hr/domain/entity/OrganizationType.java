package com.cie.hr.domain.entity;

import lombok.Getter;

import java.util.UUID;

@Getter
public class OrganizationType {
    //id nom code
    protected UUID id;
    protected String name;
    protected String code;
    protected String gradeCode;
    public OrganizationType(UUID id, String name, String code, String gradeCode) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.gradeCode = gradeCode;
    }
}
