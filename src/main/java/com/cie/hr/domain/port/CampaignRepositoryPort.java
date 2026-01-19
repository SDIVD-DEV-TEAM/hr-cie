package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Campaign;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampaignRepositoryPort extends AbstractRepository<Campaign, UUID> {

    boolean checkIfEndDateGreaterThan(Date date);

    Optional<Campaign> findFirstByStatusCode(String code);

    List<Campaign> findByStatusCodeIn(List<String> code);

    Optional<Campaign> findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc(String code);
}
