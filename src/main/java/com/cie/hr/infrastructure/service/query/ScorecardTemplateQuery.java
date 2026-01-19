package com.cie.hr.infrastructure.service.query;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.infrastructure.mapper.ScorecardTemplateMapper;
import com.cie.hr.infrastructure.repository.ScorecardExpertTemplateJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardManagerTemplateJpaRepository;
import com.cie.hr.infrastructure.repository.ScorecardTemplateJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardExpertTemplateVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardManagerTemplateVm;
import com.cie.hr.infrastructure.service.viewmodel.ScorecardTemplateVm;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
@Service
public class ScorecardTemplateQuery {

    private final ScorecardTemplateJpaRepository scorecardTemplateJpaRepository;
    private final ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository;
    private final ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository;

    public ScorecardTemplateQuery(ScorecardTemplateJpaRepository scorecardTemplateJpaRepository, ScorecardManagerTemplateJpaRepository scorecardManagerTemplateJpaRepository, ScorecardExpertTemplateJpaRepository scorecardExpertTemplateJpaRepository) {
        this.scorecardTemplateJpaRepository = scorecardTemplateJpaRepository;
        this.scorecardManagerTemplateJpaRepository = scorecardManagerTemplateJpaRepository;
        this.scorecardExpertTemplateJpaRepository = scorecardExpertTemplateJpaRepository;
    }

    public List<ScorecardTemplateVm> readAll() {
        return scorecardTemplateJpaRepository.findAll().stream().map(ScorecardTemplateMapper::toVm).toList();
    }

    public ScorecardManagerTemplateVm viewDetailsManagerForm(UUID id) {
        return scorecardManagerTemplateJpaRepository.findById(id).map(ScorecardTemplateMapper::toVm).orElseThrow(() -> new ApplicationException("No ScorecardManagerTemplate found with ID: " + id));
    }

    public ScorecardExpertTemplateVm viewDetailsExpertForm(UUID id) {
        return scorecardExpertTemplateJpaRepository.findById(id).map(ScorecardTemplateMapper::toVm).orElseThrow(() -> new ApplicationException("No ScorecardManagerTemplate found with ID: " + id));
    }

    public Object getScorecardTemplateByType(String type) {
        if (type.equals("CE")) {
            return scorecardExpertTemplateJpaRepository.findFirstByTypeAndActiveTrue(2).map(ScorecardTemplateMapper::toVm).orElseThrow(() -> new ApplicationException("No ScorecardExpertTemplate found"));
        } else {
            return scorecardManagerTemplateJpaRepository.findFirstByTypeAndActiveTrue(1).map(ScorecardTemplateMapper::toVm).orElseThrow(() -> new ApplicationException("No ScorecardManagerTemplate found"));
        }
    }
}
