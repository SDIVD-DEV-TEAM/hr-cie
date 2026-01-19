package com.cie.hr.infrastructure.service.query;

import com.cie.hr.domain.valueobject.ScoreRange;
import com.cie.hr.infrastructure.mapper.ScoreRangeMapper;
import com.cie.hr.infrastructure.repository.ScoreRangeJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Service
public class ScoreRangeQuery {

    private final ScoreRangeJpaRepository scoreRangeJpaRepository;

    public ScoreRangeQuery(ScoreRangeJpaRepository scoreRangeJpaRepository) {
        this.scoreRangeJpaRepository = scoreRangeJpaRepository;
    }

    public List<ScoreRange> getAllScoreRanges() {
        return scoreRangeJpaRepository.findAll().stream().map(ScoreRangeMapper::toScoreRangeDomain).toList();
    }
}
