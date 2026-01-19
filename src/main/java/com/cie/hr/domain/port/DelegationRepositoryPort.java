package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Delegation;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public interface DelegationRepositoryPort extends AbstractRepository<Delegation, UUID> {
    Optional<Delegation> findByEmployeeIdAndCampaignId(UUID id, UUID campaignId);

    boolean deleteDelegation(UUID id);
}
