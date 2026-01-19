package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Derogation;

import java.util.List;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
public interface DerogationRepositoryPort extends AbstractRepository<Derogation, UUID> {

    List<Derogation> findByEmployeeIdAndCampaignId(UUID employeeId, UUID campaignId);
}
