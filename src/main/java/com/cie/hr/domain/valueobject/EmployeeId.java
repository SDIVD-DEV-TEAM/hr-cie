package com.cie.hr.domain.valueobject;

import com.cie.hr.common.valueobject.BaseId;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
public class EmployeeId extends BaseId<UUID> {
    public EmployeeId(UUID value) {
        super(value);
    }
}
