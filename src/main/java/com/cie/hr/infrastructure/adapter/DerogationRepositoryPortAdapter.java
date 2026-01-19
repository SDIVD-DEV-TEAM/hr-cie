package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Derogation;
import com.cie.hr.domain.port.DerogationRepositoryPort;
import com.cie.hr.infrastructure.entity.DerogationEntity;
import com.cie.hr.infrastructure.mapper.DerogationMapper;
import com.cie.hr.infrastructure.repository.DerogationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Transactional
@Service
public class DerogationRepositoryPortAdapter implements DerogationRepositoryPort {

    private final DerogationJpaRepository derogationJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public DerogationRepositoryPortAdapter(DerogationJpaRepository derogationJpaRepository) {
        this.derogationJpaRepository = derogationJpaRepository;
    }

    @Override
    public void save(Derogation domain) {
        var derogationEntity = DerogationMapper.toEntity(domain);
        derogationJpaRepository.save(derogationEntity);
    }

    @Override
    public Optional<Derogation> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public void updateAndSave(Derogation domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                DerogationEntity derogationEntity = derogationJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette délégation n'existe pas"));
                DerogationMapper.updateAndSave(domain, derogationEntity);
                derogationJpaRepository.save(derogationEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update derogation after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating derogation", e);
                throw e;
            }
        }
    }

    @Override
    public List<Derogation> findByEmployeeIdAndCampaignId(UUID employeeId, UUID campaignId) {
        return derogationJpaRepository.findByEmployeeIdAndCampaignId(employeeId, campaignId).stream().map(DerogationMapper::toDomain).toList();
    }
}
