package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.ScorecardDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 11/05/2023
 * @project hr-cie
 */
public interface ScoreCardRepositoryPort extends AbstractRepository<ScorecardDomain, UUID> {

    void saveAll(List<ScorecardDomain> scorecards);

    List<ScorecardDomain> findByCampaignId(UUID campaignId);

    Optional<ScorecardDomain> findByAssessed_IdAndCampaignId(UUID id, UUID campaignId);

    void delete(ScorecardDomain scorecardDomain);
}
