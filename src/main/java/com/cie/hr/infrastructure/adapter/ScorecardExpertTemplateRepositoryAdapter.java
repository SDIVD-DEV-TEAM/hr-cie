package com.cie.hr.infrastructure.adapter;

import com.cie.hr.domain.port.ScorecardExpertTemplateRepositoryPort;
import com.cie.hr.infrastructure.mapper.ScorecardTemplateMapper;
import com.cie.hr.infrastructure.repository.ScorecardExpertTemplateJpaRepository;
import com.cie.hr.infrastructure.valueobject.ScorecardForExpert;
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
public class ScorecardExpertTemplateRepositoryAdapter implements ScorecardExpertTemplateRepositoryPort {

    private final ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository;

    public ScorecardExpertTemplateRepositoryAdapter(ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository) {
        this.scorecardExpertTemplateJpaRepository = scorecardExpertTemplateJpaRepository;
    }

    @Override
    public Optional<ScorecardForExpert> findFirstByTypeAndActiveTrue(Integer type) {
        return scorecardExpertTemplateJpaRepository.findFirstByTypeAndActiveTrue(type).map(ScorecardTemplateMapper::toExpertDomain);
    }
}
