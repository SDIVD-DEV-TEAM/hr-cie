package com.cie.hr.domain.valueobject;

import lombok.Getter;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
@Getter
public enum AccessLevelEnum {
    Level_0("Niveau 0"),
    Level_1("Niveau 1"),
    Level_2("Niveau 2"),
    Level_3("Niveau 3"),
    Level_4("Niveau 4");

    public final String label;

    AccessLevelEnum(String label) {
        this.label = label;
    }

}
