package com.cie.hr.infrastructure.service.viewmodel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 23/06/2023
 * @project hr-cie
 */
public record DisputesChatsVm(
        UUID id,
        String subject,
        String message,
        LocalDateTime createdAt
) {
}
