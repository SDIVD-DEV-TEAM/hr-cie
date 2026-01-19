package com.cie.hr.infrastructure.service.viewmodel;

import java.util.List;
import java.util.UUID;

public record OrganizationListVm(
        UUID id,
        String name,
        String code,
        UUID typeId,
        String typeName,
        List<OrganizationListDetails> directions,
        List<JobWithEmployeeInfoVm> jobs
) {

}


