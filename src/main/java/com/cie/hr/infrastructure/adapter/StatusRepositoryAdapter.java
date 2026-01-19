package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Status;
import com.cie.hr.domain.port.StatusRepositoryPort;
import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.mapper.StatusMapper;
import com.cie.hr.infrastructure.repository.StatusJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@Service
public class StatusRepositoryAdapter implements StatusRepositoryPort {

    private final StatusJpaRepository statusJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public StatusRepositoryAdapter(StatusJpaRepository statusJpaRepository) {
        this.statusJpaRepository = statusJpaRepository;
    }

    @Override
    public void save(Status entity) {
        statusJpaRepository.save(StatusMapper.toStatusEntity(entity));
    }

    @Override
    public Optional<Status> findById(UUID id) {
        var status = this.statusJpaRepository.findById(id);
        return status.map(StatusMapper::toStatusDomain);
    }

    @Override
    public void updateAndSave(Status domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                StatusEntity statusEntity = statusJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Ce statut n'existe pas"));
                StatusMapper.updateAndSave(domain, statusEntity);
                statusJpaRepository.save(statusEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update status after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating status", e);
                throw e;
            }
        }
    }

    @Override
    public Optional<Status> findByCode(String code) {
        var status = this.statusJpaRepository.findByCode(code);
        return status.map(StatusMapper::toStatusDomain);
    }

    @Override
    public Optional<Status> findByName(String name) {
        var status = this.statusJpaRepository.findByName(name);
        return status.map(StatusMapper::toStatusDomain);
    }

}
