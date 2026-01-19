package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

public record StatusVm(
        UUID id,
        String name,
        String code
) {
}
