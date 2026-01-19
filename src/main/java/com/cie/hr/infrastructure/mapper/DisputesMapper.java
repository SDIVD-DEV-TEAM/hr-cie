package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Disputes;
import com.cie.hr.domain.entity.DisputesChats;
import com.cie.hr.infrastructure.entity.DisputesChatsEntity;
import com.cie.hr.infrastructure.entity.DisputesEntity;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.service.viewmodel.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
public class DisputesMapper {

    public static DisputesEntity toDisputeEntity(Disputes disputes) {

        if (disputes == null) {
            return null;
        }

        List<DisputesChatsEntity> disputesEntities;
        var dispute = DisputesEntity.builder()
                .employee(EmployeeMapper.toEmployeeEntity(disputes.getEmployee()))
                .scorecard(ScorecardMapper.toEntity(disputes.getScorecard()))
                .build();
        if (disputes.getMessages() != null) {
            disputesEntities = disputes.getMessages().stream().map(e -> DisputesChatsEntity.builder()
                    .isRejected(e.isRejected())
                    .createdAt(e.getCreatedAt())
                    .message(e.getMessage())
                    .id(e.getId())
                    .message(e.getMessage())
                    .build()).collect(Collectors.toList());

            dispute.setDisputesChats(disputesEntities);
        }
        dispute.setId(disputes.getId());
        return dispute;
    }

    public static Disputes toDisputesDomain(DisputesEntity disputesEntity) {
        if (disputesEntity == null) {
            return null;
        }
        Disputes.Builder disputes = Disputes.newBuilder();
        disputes.id(disputesEntity.getId());
        disputes.employee(EmployeeMapper.toEmployeeDomain(disputesEntity.getEmployee()));
        disputes.scorecard(ScorecardMapper.toDomain(disputesEntity.getScorecard()));
        disputes.messages(disputesEntity.getDisputesChats() == null ? new ArrayList<>() :
                disputesEntity.getDisputesChats().stream().map(e -> DisputesChats.newBuilder()
                        .id(e.getId())
                        .subject(e.getSubject())
                        .message(e.getMessage())
                        .createdAt(e.getCreatedAt())
                        .isRejected(e.isRejected())
                        .build()).collect(Collectors.toList()));
        return disputes.build();
    }

    public static void updateAndSave(Disputes domain, DisputesEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        EmployeeEntity employeeEntity = EmployeeMapper.toEmployeeEntity(domain.getEmployee());
        if (employeeEntity != null && (entity.getEmployee() == null ||
                !Objects.equals(entity.getEmployee(), employeeEntity))) {
            entity.setEmployee(employeeEntity);
        }

        ScorecardEntity scorecardEntity = ScorecardMapper.toEntity(domain.getScorecard());
        if (scorecardEntity != null && (entity.getScorecard() == null ||
                !Objects.equals(entity.getScorecard(), scorecardEntity))) {
            entity.setScorecard(scorecardEntity);
        }


        if (domain.getMessages() != null) {
            entity.setDisputesChats(domain.getMessages().stream().map(e -> DisputesChatsEntity.builder()
                    .isRejected(e.isRejected())
                    .createdAt(e.getCreatedAt())
                    .message(e.getMessage())
                    .id(e.getId())
                    .message(e.getMessage())
                    .build()).collect(Collectors.toList()));
        }
    }

    public static DisputeOnlyVm toDisputeOnlyVm(DisputesEntity disputesEntity) {
        return new DisputeOnlyVm(
                disputesEntity.getId(),
                new ScorecardVm(
                        disputesEntity.getScorecard().getId(),
                        disputesEntity.getScorecard().getCampaign().getName(),
                        disputesEntity.getScorecard().getManager() == null ? null : disputesEntity.getScorecard().getManager().getFullName())
        );
    }

    public static DisputesVm toDisputesVm(DisputesEntity disputesEntity) {
        List<DisputesChatsVm> disputesChatsVms = new ArrayList<>();
        disputesEntity.getDisputesChats().forEach(e -> disputesChatsVms.add(new DisputesChatsVm(e.getId(), e.getSubject(), e.getMessage(), e.getCreatedAt())));
        return new DisputesVm(
                disputesEntity.getId(),
                new ScorecardVm(
                        disputesEntity.getScorecard().getId(),
                        disputesEntity.getScorecard().getCampaign().getName(),
                        disputesEntity.getScorecard().getManager() == null ? null : disputesEntity.getScorecard().getManager().getFullName()),
                new EmployeeVm(
                        disputesEntity.getEmployee().getId(),
                        disputesEntity.getEmployee().getLastname(),
                        disputesEntity.getEmployee().getFirstname(),
                        disputesEntity.getEmployee().getEmployeeNumber(),
                        disputesEntity.getEmployee().getEmail(),
                        disputesEntity.getEmployee().getProfile().getName(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        !disputesEntity.getEmployee().getIsNotLocked()
                ),
                disputesChatsVms
        );

    }
}
