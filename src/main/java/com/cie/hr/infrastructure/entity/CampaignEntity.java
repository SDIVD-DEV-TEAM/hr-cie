package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "campaigns")
public class CampaignEntity extends AbstractEntity {
    @Column
    private String name;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "status_id", foreignKey = @ForeignKey(name = "fk_campaigns_status"))
    private StatusEntity status;
}
