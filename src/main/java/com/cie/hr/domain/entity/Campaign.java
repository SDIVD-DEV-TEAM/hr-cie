package com.cie.hr.domain.entity;

import com.cie.hr.common.exception.DomainException;
import com.cie.hr.domain.port.CampaignRepositoryPort;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;


@Getter
@Setter
public class Campaign {
    private UUID id;
    private String name;
    private Date startDate;
    private Date endDate;
    private boolean deleted;

    private Status status;


    public Campaign(UUID id, String name, Date startDate, Date endDate, Status status, boolean deleted) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.deleted = deleted;
    }

    public void checkBusinessRules(CampaignRepositoryPort campaignRepositoryPort) {
        if (this.startDate.before(new Date())) {
            throw new DomainException("The start date must be older than today");
        }

        var result = campaignRepositoryPort.checkIfEndDateGreaterThan(this.startDate);
        if (result) {
            throw new DomainException("The date: " + this.startDate + "is already include in another campaign");
        }

        if (this.startDate.after(endDate)) {
            throw new DomainException("The start date must be older than the end date");
        }
    }

}
