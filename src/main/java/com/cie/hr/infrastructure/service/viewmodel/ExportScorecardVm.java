package com.cie.hr.infrastructure.service.viewmodel;

/**
 * @author Alexis TAMBIE
 * @created 20/09/2024
 * @project hr-cie
 */
public record ExportScorecardVm(
        String employee_number,
        String lastname,
        String firstname,
        String organization_code,
        String job_title,
        String job_code,
        double section_a_sum_note,
        double section_a_sum_coefficient,
        double section_a_note_weighted,
        double section_a_coefficient,
        double section_a_note,
        double section_b_sum_note,
        double section_b_sum_coefficient,
        double section_b_note_weighted,
        double section_b_coefficient,
        double section_b_note,
        double section_c_sum_note,
        double section_c_sum_coefficient,
        double section_c_note_weighted,
        double section_c_coefficient,
        double section_c_note,
        double section_d_sum_note,
        double section_d_sum_coefficient,
        double section_d_note_weighted,
        double section_d_coefficient,
        double section_d_note,
        double total_note,
        double total_coefficient,
        double total_note_weighted,
        String mobility_status,
        String mobility_reason,
        String formation_status,
        String formation_reason,
        String analyse_one_reason,
        String analyse_one_main_difficulty,
        String analyse_one_manager_comment,
        String analyse_one_possible_improvements,
        String analyse_two_reason,
        String analyse_two_main_difficulty,
        String analyse_two_manager_comment,
        String analyse_two_possible_improvements,
        String analyse_three_reason,
        String analyse_three_main_difficulty,
        String analyse_three_manager_comment,
        String analyse_three_possible_improvements,
        String manager_employee_number,
        String manager_lastname,
        String manager_firstname,
        String status
) {
}
