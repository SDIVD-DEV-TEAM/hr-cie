package com.cie.hr.common.event;

import com.cie.hr.common.domain.event.EmployeeDomainEvent;
import com.cie.hr.domain.entity.EmployeeDomain;

import java.time.ZonedDateTime;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
public class CreateEmployeeEvent extends EmployeeDomainEvent {
    public CreateEmployeeEvent(EmployeeDomain employee, ZonedDateTime createdAt) {
        super(employee, createdAt);
    }
}
