package com.cie.hr.infrastructure.service.viewmodel;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 21/06/2023
 * @project hr-cie
 */
public record OrganizationListDetails(
        UUID id,
        String name,
        String code,
        UUID parentId,
        String parentName,
        UUID typeId,
        String typeName,
        String managerName,
        String managerJob,
        Integer countDirection,
        Integer countToEvaluate,
        Integer countEvaluate,
        boolean hasDirection,
        boolean isJob
) {
}
