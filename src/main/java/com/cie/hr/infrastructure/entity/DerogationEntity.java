package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */

@Entity
@Table(name = "derogations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DerogationEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private EmployeeEntity manger;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private CampaignEntity campaign;

    @Column(name = "expired_at")
    private LocalDate expiredAt;

}
