package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.port.MobilityRepositoryPort;
import com.cie.hr.domain.valueobject.Mobility;
import com.cie.hr.infrastructure.entity.MobilityEntity;
import com.cie.hr.infrastructure.mapper.MobilityMapper;
import com.cie.hr.infrastructure.repository.MobilityJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Transactional
@Service
public class MobilityRepositoryAdapter implements MobilityRepositoryPort {

    private final MobilityJpaRepository mobilityJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public MobilityRepositoryAdapter(MobilityJpaRepository mobilityJpaRepository) {
        this.mobilityJpaRepository = mobilityJpaRepository;
    }

    @Override
    public void save(Mobility mobility) {
        MobilityEntity mobilityEntity = MobilityMapper.toMobilityEntity(mobility);
        mobilityJpaRepository.save(mobilityEntity);
    }

    @Override
    public Optional<Mobility> findById(UUID id) {
        var mobility = mobilityJpaRepository.findById(id);
        return mobility.map(MobilityMapper::toMobilityDomain);
    }

    @Override
    public void updateAndSave(Mobility domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                MobilityEntity mobilityEntity = mobilityJpaRepository.findByIdForWrite(domain.id()).orElseThrow(() -> new ApplicationException("Cette mobilité n'existe pas"));
                MobilityMapper.updateAndSave(domain, mobilityEntity);
                mobilityJpaRepository.save(mobilityEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update mobility after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating mobility", e);
                throw e;
            }
        }
    }
}
