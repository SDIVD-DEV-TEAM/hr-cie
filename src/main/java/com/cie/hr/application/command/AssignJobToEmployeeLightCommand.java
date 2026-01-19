package com.cie.hr.application.command;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/05/2023
 * @project hr-cie
 */
public record AssignJobToEmployeeLightCommand(
        UUID employeeId
) {
}
