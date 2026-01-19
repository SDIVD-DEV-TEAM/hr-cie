package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.ScoreNoteDistribution;
import com.cie.hr.domain.port.ScoreNoteDistributionRepositoryPort;
import com.cie.hr.infrastructure.entity.ScoreNoteDistributionEntity;
import com.cie.hr.infrastructure.mapper.ScoreNoteDistributionMapper;
import com.cie.hr.infrastructure.repository.ScoreNoteDistributionJpaRepository;
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
public class ScoreNoteDistributionRepositoryPortAdapter implements ScoreNoteDistributionRepositoryPort {

    private final ScoreNoteDistributionJpaRepository scoreNoteDistributionJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ScoreNoteDistributionRepositoryPortAdapter(ScoreNoteDistributionJpaRepository scoreNoteDistributionJpaRepository) {
        this.scoreNoteDistributionJpaRepository = scoreNoteDistributionJpaRepository;
    }

    @Override
    public void save(ScoreNoteDistribution entity) {
        scoreNoteDistributionJpaRepository.save(ScoreNoteDistributionMapper.toEntity(entity));
    }

    @Override
    public Optional<ScoreNoteDistribution> findById(UUID id) {
        return scoreNoteDistributionJpaRepository.findById(id).map(ScoreNoteDistributionMapper::toDomain);
    }

    @Override
    public void updateAndSave(ScoreNoteDistribution domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                ScoreNoteDistributionEntity scorecardEntity = scoreNoteDistributionJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette note n'existe pas"));
                ScoreNoteDistributionMapper.updateAndSave(domain, scorecardEntity);
                scoreNoteDistributionJpaRepository.save(scorecardEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update scorecard after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating scorecard", e);
                throw e;
            }
        }
    }

    @Override
    public Optional<ScoreNoteDistribution> findFirstByValueInRangeAndNoteDistributionId(double value, UUID id) {
        return scoreNoteDistributionJpaRepository.findFirstByValueInRangeAndNoteDistributionId(value, id).map(ScoreNoteDistributionMapper::toDomain);
    }
}
