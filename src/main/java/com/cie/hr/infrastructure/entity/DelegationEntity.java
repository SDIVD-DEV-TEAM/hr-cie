package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Entity
@Getter
@Setter
@Table(name = "delegations")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelegationEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private CampaignEntity campaign;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private EmployeeEntity receiver;

    @ManyToOne
    @JoinColumn(name = "giver_id")
    private EmployeeEntity giver;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
