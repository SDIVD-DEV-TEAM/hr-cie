package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.DerogationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 11/05/2023
 * @project hr
 */
@Repository
public interface DerogationJpaRepository extends JpaRepository<DerogationEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM DerogationEntity e WHERE e.id = :id")
    Optional<DerogationEntity> findByIdForWrite(@Param("id") UUID id);

    List<DerogationEntity> findByEmployeeIdAndCampaignId(UUID employeeId, UUID campaignId);

    List<DerogationEntity> findAllByExpiredAtBefore(LocalDate expiredAt);

    List<DerogationEntity> findAllByMangerIdAndDeletedFalse(UUID mangerId);
}
