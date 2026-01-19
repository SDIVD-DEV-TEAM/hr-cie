package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Delegation;
import com.cie.hr.domain.port.DelegationRepositoryPort;
import com.cie.hr.infrastructure.entity.DelegationEntity;
import com.cie.hr.infrastructure.mapper.DelegationMapper;
import com.cie.hr.infrastructure.repository.DelegationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */

@Transactional
@Service
public class DelegationRepositoryPortAdapter implements DelegationRepositoryPort {

    private final DelegationJpaRepository delegationJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public DelegationRepositoryPortAdapter(DelegationJpaRepository delegationJpaRepository) {
        this.delegationJpaRepository = delegationJpaRepository;
    }

    @Override
    public void save(Delegation entity) {
        delegationJpaRepository.save(DelegationMapper.toEntity(entity));
    }

    @Override
    public Optional<Delegation> findById(UUID id) {
        return delegationJpaRepository.findById(id).map(DelegationMapper::toDomain);
    }

    @Override
    public void updateAndSave(Delegation domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                DelegationEntity delegationEntity = delegationJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette délégation n'existe pas"));
                DelegationMapper.updateAndSave(domain, delegationEntity);
                delegationJpaRepository.save(delegationEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update delegation after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating delegation", e);
                throw e;
            }
        }
    }

    @Override
    public Optional<Delegation> findByEmployeeIdAndCampaignId(UUID id, UUID delegationId) {
        return delegationJpaRepository.findByEmployeeIdAndCampaignIdAndDeletedFalse(id, delegationId).map(DelegationMapper::toDomain);
    }

    @Override
    public boolean deleteDelegation(UUID id) {
        try {
            delegationJpaRepository.deleteById(id);
            return true;
        } catch (Exception ex) {
            return false;
        }

    }
}
