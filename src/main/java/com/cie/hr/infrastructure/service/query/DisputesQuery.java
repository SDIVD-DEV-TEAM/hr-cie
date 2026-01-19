package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.mapper.DisputesMapper;
import com.cie.hr.infrastructure.repository.DisputesJpaRepository;
import com.cie.hr.infrastructure.repository.JobJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.DisputesChatsVm;
import com.cie.hr.infrastructure.service.viewmodel.DisputesVm;
import com.cie.hr.infrastructure.service.viewmodel.EmployeeVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardVm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Service
public class DisputesQuery {

    private final DisputesJpaRepository disputesJpaRepository;

    private final JobJpaRepository jobJpaRepository;

    public DisputesQuery(DisputesJpaRepository disputesJpaRepository, JobJpaRepository jobJpaRepository) {
        this.disputesJpaRepository = disputesJpaRepository;
        this.jobJpaRepository = jobJpaRepository;
    }

    public Optional<DisputesVm> disputesDetail(UUID id) {
        var dispute = disputesJpaRepository.findById(id);
        if (dispute.isEmpty()) {
            return Optional.empty();
        } else {
            DisputesVm disputeVm;
            List<DisputesChatsVm> disputesChatsVms = new ArrayList<>();
            dispute.get().getDisputesChats().forEach(e -> disputesChatsVms.add(new DisputesChatsVm(e.getId(), e.getSubject(), e.getMessage(), e.getCreatedAt())));
            var jobEmployee = jobJpaRepository.findByEmployeeId(dispute.get().getEmployee().getId());
            disputeVm = jobEmployee.map(jobEntity -> new DisputesVm(
                    dispute.get().getId(),
                    new ScorecardVm(
                            dispute.get().getScorecard().getId(), dispute.get().getScorecard().getCampaign().getName(),
                            dispute.get().getScorecard().getManager() == null ? null : dispute.get().getScorecard().getManager().getFullName()
                    ),
                    new EmployeeVm(
                            jobEntity.getEmployee().getId(),
                            jobEntity.getEmployee().getLastname(),
                            jobEntity.getEmployee().getFirstname(),
                            jobEntity.getEmployee().getEmployeeNumber(),
                            jobEntity.getEmployee().getEmail(),
                            jobEntity.getEmployee().getProfile().getName(),
                            jobEntity.getTitle(),
                            jobEntity.getGrade().getName(),
                            null,
                            null,
                            null,
                            !jobEntity.getEmployee().getIsNotLocked()
                    ),
                    disputesChatsVms
            )).orElseGet(() -> DisputesMapper.toDisputesVm(dispute.get()));
            return Optional.of(disputeVm);
        }
    }

    public List<DisputesVm> disputesDetailByScorecard(UUID id) {
        return disputesJpaRepository.findFirstByScorecardId(id).stream().map(DisputesMapper::toDisputesVm).collect(Collectors.toList());
    }
}
