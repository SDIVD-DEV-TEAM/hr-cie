package com.cie.hr.common.domain.event;

import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
@Getter
public abstract class EmployeeDomainEvent implements DomainEvent<EmployeeEntity> {

    private final EmployeeDomain employee;
    private final ZonedDateTime createdAt;

    public EmployeeDomainEvent(EmployeeDomain employee, ZonedDateTime createdAt) {
        this.employee = employee;
        this.createdAt = createdAt;
    }

}
