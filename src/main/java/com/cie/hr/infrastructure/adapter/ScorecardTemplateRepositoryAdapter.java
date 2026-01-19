package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.ScorecardTemplate;
import com.cie.hr.domain.port.ScorecardTemplateRepositoryPort;
import com.cie.hr.infrastructure.entity.ScorecardTemplateEntity;
import com.cie.hr.infrastructure.mapper.ScorecardTemplateMapper;
import com.cie.hr.infrastructure.repository.ScorecardTemplateJpaRepository;
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
 * @created 10/05/2023
 * @project hr
 */
@Transactional
@Service
public class ScorecardTemplateRepositoryAdapter implements ScorecardTemplateRepositoryPort {

    private final ScorecardTemplateJpaRepository scorecardTemplateJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ScorecardTemplateRepositoryAdapter(ScorecardTemplateJpaRepository scorecardTemplateJpaRepository) {
        this.scorecardTemplateJpaRepository = scorecardTemplateJpaRepository;
    }

    @Override
    public void save(ScorecardTemplate entity) {
        var scorecardTemplate = ScorecardTemplateMapper.toEntity(entity);
        scorecardTemplateJpaRepository.save(scorecardTemplate);
    }

    @Override
    public Optional<ScorecardTemplate> findById(UUID id) {
        // var  scorecardTemplate =  scorecardTemplateJpaRepository.findById(id);
        return Optional.empty(); //Optional.of(ScorecardTemplateMapper.toManagerDomain(scorecardTemplate.get()));
    }

    @Override
    public void updateAndSave(ScorecardTemplate domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                ScorecardTemplateEntity scorecardEntity = scorecardTemplateJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette fiche d'évaluation n'existe pas"));
                ScorecardTemplateMapper.updateAndSave(domain, scorecardEntity);
                scorecardTemplateJpaRepository.save(scorecardEntity);
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
    public void saveAll(List<ScorecardTemplate> entities) {
        List<ScorecardTemplateEntity> scorecardTemplateEntities = entities.stream().map(ScorecardTemplateMapper::toEntity).toList();
        scorecardTemplateJpaRepository.saveAll(scorecardTemplateEntities);
    }
}
