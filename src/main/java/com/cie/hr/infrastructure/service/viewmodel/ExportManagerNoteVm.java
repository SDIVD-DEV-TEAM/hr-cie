package com.cie.hr.infrastructure.service.viewmodel;

/**
 * @author Alexis TAMBIE
 * @created 10/01/2025
 * @project hr-cie
 */
public record ExportManagerNoteVm(
        String employee_number,
        String lastname,
        String firstname,
        String job_title,
        double total_note
) {
}
