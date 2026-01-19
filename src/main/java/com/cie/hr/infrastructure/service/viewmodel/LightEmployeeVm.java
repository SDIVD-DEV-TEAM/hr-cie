package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 25/05/2023
 * @project hr-cie
 */
public record LightEmployeeVm(UUID id, String firstname, String lastname, String grade) {
}
