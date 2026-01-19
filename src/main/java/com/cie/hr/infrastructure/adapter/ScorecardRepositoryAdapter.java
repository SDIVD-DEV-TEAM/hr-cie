package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.ScorecardDomain;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.infrastructure.entity.ScorecardEntity;
import com.cie.hr.infrastructure.mapper.ScorecardMapper;
import com.cie.hr.infrastructure.repository.ScorecardJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 12/05/2023
 * @project hr-cie
 */
@Transactional
@Service
public class ScorecardRepositoryAdapter implements ScoreCardRepositoryPort {

    private final ScorecardJpaRepository scorecardJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ScorecardRepositoryAdapter(ScorecardJpaRepository scorecardJpaRepository) {
        this.scorecardJpaRepository = scorecardJpaRepository;
    }

    @Override
    public void save(ScorecardDomain scorecard) {
        scorecardJpaRepository.save(ScorecardMapper.toEntity(scorecard));
    }

    @Override
    public Optional<ScorecardDomain> findById(UUID id) {
        return scorecardJpaRepository.findById(id).map(ScorecardMapper::toDomain);
    }

    @Override
    public void updateAndSave(ScorecardDomain domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                ScorecardEntity scorecardEntity = scorecardJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette fiche d'évaluation n'existe pas"));
                ScorecardMapper.updateAndSave(domain, scorecardEntity);
                scorecardJpaRepository.save(scorecardEntity);
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
    public void saveAll(List<ScorecardDomain> scorecards) {
        List<ScorecardEntity> scorecardEntities;
        scorecardEntities = scorecards.stream().map(ScorecardMapper::toEntity).toList();
        scorecardJpaRepository.saveAll(scorecardEntities);
    }

    @Override
    public List<ScorecardDomain> findByCampaignId(UUID campaignId) {
        return scorecardJpaRepository.findByDeletedFalseAndCampaignId(campaignId).stream().map(ScorecardMapper::toDomain).toList();
    }

    @Override
    public Optional<ScorecardDomain> findByAssessed_IdAndCampaignId(UUID id, UUID campaignId) {
        return scorecardJpaRepository.findByDeletedFalseAndAssessedIdAndCampaignId(id, campaignId).map(ScorecardMapper::toDomain);
    }

    @Override
    public void delete(ScorecardDomain scorecardDomain) {
        scorecardJpaRepository.delete(ScorecardMapper.toEntity(scorecardDomain));
    }
}
