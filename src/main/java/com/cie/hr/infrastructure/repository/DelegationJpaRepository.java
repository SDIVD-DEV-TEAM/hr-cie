package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.DelegationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Repository
public interface DelegationJpaRepository extends JpaRepository<DelegationEntity, UUID> {
    Optional<DelegationEntity> findByEmployeeIdAndCampaignIdAndDeletedFalse(UUID id, UUID campaignId);

    List<DelegationEntity> findByCampaignIdAndReceiverIdAndDeletedFalse(UUID campaignId, UUID employeeId);

    List<DelegationEntity> findByCampaignIdAndGiverIdAndDeletedFalse(UUID campaignId, UUID employeeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM DelegationEntity e WHERE e.id = :id")
    Optional<DelegationEntity> findByIdForWrite(@Param("id") UUID id);
}
