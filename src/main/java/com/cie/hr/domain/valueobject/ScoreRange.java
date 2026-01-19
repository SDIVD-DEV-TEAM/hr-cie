package com.cie.hr.domain.valueobject;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
public record ScoreRange(UUID id, String label, double max_range, double min_range) {
}
