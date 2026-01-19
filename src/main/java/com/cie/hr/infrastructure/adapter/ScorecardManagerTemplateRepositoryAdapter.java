package com.cie.hr.infrastructure.adapter;

import com.cie.hr.domain.port.ScorecardManagerTemplateRepositoryPort;
import com.cie.hr.infrastructure.mapper.ScorecardTemplateMapper;
import com.cie.hr.infrastructure.repository.ScorecardManagerTemplateJpaRepository;
import com.cie.hr.infrastructure.valueobject.ScorecardForManagerForm;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Alexis TAMBIE
 * @created 07/07/2023
 * @project hr-cie
 */
@Transactional
@Service
public class ScorecardManagerTemplateRepositoryAdapter implements ScorecardManagerTemplateRepositoryPort {

    private final ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository;

    public ScorecardManagerTemplateRepositoryAdapter(ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository) {
        this.scorecardManagerTemplateJpaRepository = scorecardManagerTemplateJpaRepository;
    }

    @Override
    public Optional<ScorecardForManagerForm> findFirstByTypeAndActiveTrue(Integer type) {
        return scorecardManagerTemplateJpaRepository.findFirstByTypeAndActiveTrue(type).map(ScorecardTemplateMapper::toManagerDomain);
    }
}
