package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.ScorecardTemplate;

import java.util.List;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
public interface ScorecardTemplateRepositoryPort extends AbstractRepository<ScorecardTemplate, UUID> {
    void saveAll(List<ScorecardTemplate> entities);
}
