package com.cie.hr.infrastructure.entity;

import com.cie.hr.common.adapter.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="disputes")
public class DisputesEntity extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "employee_id", foreignKey = @ForeignKey(name = "fk_disputes_employee"), nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "scorecard_id", foreignKey = @ForeignKey(name = "fk_disputes_scorecard"), nullable = false)
    private ScorecardEntity scorecard;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "disputes_id", foreignKey = @ForeignKey(name = "fk_disputes_chats"), nullable = false)
    List<DisputesChatsEntity> disputesChats;
}
