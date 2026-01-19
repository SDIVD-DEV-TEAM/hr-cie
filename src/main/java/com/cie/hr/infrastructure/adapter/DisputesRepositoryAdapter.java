package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Disputes;
import com.cie.hr.domain.port.DisputeRepositoryPort;
import com.cie.hr.infrastructure.entity.DisputesEntity;
import com.cie.hr.infrastructure.mapper.DisputesMapper;
import com.cie.hr.infrastructure.repository.DisputesJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Transactional
@Service
public class DisputesRepositoryAdapter implements DisputeRepositoryPort {

    private final DisputesJpaRepository disputesJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public DisputesRepositoryAdapter(DisputesJpaRepository disputesJpaRepository) {
        this.disputesJpaRepository = disputesJpaRepository;
    }

    @Override
    public void save(Disputes entity) {
        disputesJpaRepository.save(DisputesMapper.toDisputeEntity(entity));
    }

    @Override
    public Optional<Disputes> findById(UUID id) {
        return disputesJpaRepository.findById(id).map(DisputesMapper::toDisputesDomain);
    }

    @Override
    public void updateAndSave(Disputes domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                DisputesEntity disputeEntity = disputesJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette reclamation n'existe pas"));
                DisputesMapper.updateAndSave(domain, disputeEntity);
                disputesJpaRepository.save(disputeEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update dispute after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating dispute", e);
                throw e;
            }
        }
    }

    @Override
    public Optional<Disputes> findByScorecardId(UUID scorecardId) {
        return disputesJpaRepository.findFirstByScorecardId(scorecardId).stream().findFirst().map(DisputesMapper::toDisputesDomain);
    }
}
