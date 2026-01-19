package com.cie.hr.common.event;

import com.cie.hr.common.domain.event.EmployeeDomainEvent;
import com.cie.hr.domain.entity.EmployeeDomain;

import java.time.ZonedDateTime;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
public class OnResetPasswordEvent extends EmployeeDomainEvent {
    public OnResetPasswordEvent(EmployeeDomain employee, ZonedDateTime createdAt) {
        super(employee, createdAt);
    }
}
