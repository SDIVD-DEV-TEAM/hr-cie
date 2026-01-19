package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public record GradeVm(UUID id, String name, String description, String code) {
}
