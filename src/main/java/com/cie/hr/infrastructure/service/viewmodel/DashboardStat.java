package com.cie.hr.infrastructure.service.viewmodel;

/**
 * @author Alexis TAMBIE
 * @created 22/09/2023
 * @project hr-cie
 */
public record DashboardStat(
        StatsTemplate my_evaluations,
        StatsTemplate my_team_evaluations
) {
}
