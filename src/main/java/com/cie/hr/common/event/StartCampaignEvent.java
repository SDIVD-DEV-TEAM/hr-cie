package com.cie.hr.common.event;

import com.cie.hr.common.domain.event.EmployeeDomainEvent;
import com.cie.hr.domain.entity.EmployeeDomain;

import java.time.ZonedDateTime;

/**
 * @author Alexis TAMBIE
 * @created 24/06/2023
 * @project hr-cie
 */
public class StartCampaignEvent extends EmployeeDomainEvent {
    public StartCampaignEvent(EmployeeDomain employee, ZonedDateTime createdAt) {
        super(employee, createdAt);
    }
}
