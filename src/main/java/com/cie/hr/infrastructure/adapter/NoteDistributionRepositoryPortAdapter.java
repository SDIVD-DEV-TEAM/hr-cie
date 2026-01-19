package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.NoteDistribution;
import com.cie.hr.domain.port.NoteDistributionRepositoryPort;
import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import com.cie.hr.infrastructure.mapper.NoteDistributionMapper;
import com.cie.hr.infrastructure.repository.NoteDistributionJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Transactional
@Service
public class NoteDistributionRepositoryPortAdapter implements NoteDistributionRepositoryPort {

    private final NoteDistributionJpaRepository noteDistributionJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public NoteDistributionRepositoryPortAdapter(NoteDistributionJpaRepository noteDistributionJpaRepository) {
        this.noteDistributionJpaRepository = noteDistributionJpaRepository;
    }

    @Override
    public void save(NoteDistribution entity) {
        noteDistributionJpaRepository.save(NoteDistributionMapper.toEntity(entity));
    }

    @Override
    public Optional<NoteDistribution> findById(UUID id) {
        return noteDistributionJpaRepository.findById(id).map(NoteDistributionMapper::toDomain);
    }

    @Override
    public void updateAndSave(NoteDistribution domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                NoteDistributionEntity noteDistributionEntity = noteDistributionJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette mobilité n'existe pas"));
                NoteDistributionMapper.updateAndSave(domain, noteDistributionEntity);
                noteDistributionJpaRepository.save(noteDistributionEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update noteDistribution after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating noteDistribution", e);
                throw e;
            }
        }
    }

    @Override
    public Optional<NoteDistribution> findByCode(String code) {
        return noteDistributionJpaRepository.findFirstByCode(code).map(NoteDistributionMapper::toDomain);
    }
}
