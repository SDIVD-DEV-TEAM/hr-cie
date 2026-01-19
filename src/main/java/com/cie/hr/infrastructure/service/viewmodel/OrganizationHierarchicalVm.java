package com.cie.hr.infrastructure.service.viewmodel;

import java.util.List;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 17/07/2023
 * @project hr-cie
 */
public record OrganizationHierarchicalVm(
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
        List<OrganizationListDetails> children,
        String costCenter
) {
}
