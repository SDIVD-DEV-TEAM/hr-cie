package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.port.ScoreRangeRepositoryPort;
import com.cie.hr.domain.valueobject.ScoreRange;
import com.cie.hr.infrastructure.entity.ScoreRangeEntity;
import com.cie.hr.infrastructure.mapper.ScoreRangeMapper;
import com.cie.hr.infrastructure.repository.ScoreRangeJpaRepository;
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
public class ScoreRangeRepositoryAdapter implements ScoreRangeRepositoryPort {

    private final ScoreRangeJpaRepository scoreRangeJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ScoreRangeRepositoryAdapter(ScoreRangeJpaRepository scoreRangeJpaRepository) {
        this.scoreRangeJpaRepository = scoreRangeJpaRepository;
    }

    @Override
    public void save(ScoreRange scoreRange) {
        ScoreRangeEntity scoreRangeEntity = ScoreRangeMapper.toScoreRangeEntity(scoreRange);
        scoreRangeJpaRepository.save(scoreRangeEntity);
    }

    @Override
    public Optional<ScoreRange> findById(UUID id) {
        return scoreRangeJpaRepository.findById(id).map(ScoreRangeMapper::toScoreRangeDomain);
    }

    @Override
    public void updateAndSave(ScoreRange domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                ScoreRangeEntity scorecardEntity = scoreRangeJpaRepository.findByIdForWrite(domain.id()).orElseThrow(() -> new ApplicationException("Cette note n'existe pas"));
                ScoreRangeMapper.updateAndSave(domain, scorecardEntity);
                scoreRangeJpaRepository.save(scorecardEntity);
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
}
