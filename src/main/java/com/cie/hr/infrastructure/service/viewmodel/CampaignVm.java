package com.cie.hr.infrastructure.service.viewmodel;

import java.util.Date;
import java.util.UUID;

public record CampaignVm(
        UUID id,
        String name,
        Date start_date,
        Date end_date,
        StatusVm status,
        boolean canBeModified
) {
}
