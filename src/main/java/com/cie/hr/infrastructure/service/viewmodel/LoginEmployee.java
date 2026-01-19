package com.cie.hr.infrastructure.service.viewmodel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 05/05/2023
 * @project hr-cie
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoginEmployee {
    UUID id;
    String lastName;
    String firstName;
    String employeeNumber;
    boolean firstConnexion;
    String email;
    String token;
    boolean isActive;
    boolean hasEvaluation;
    boolean hasPerformances;
    String jobTitle;
}
