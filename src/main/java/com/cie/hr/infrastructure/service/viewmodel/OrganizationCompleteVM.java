package com.cie.hr.infrastructure.service.viewmodel;

import java.util.List;

/**
 * @author Koty BLEU
 * @created 23/05/2023
 * @project hr
 */
public record OrganizationCompleteVM(OrganizationVm organization, List<JobVm> jobs, List<OrganizationVm> children) {
}
