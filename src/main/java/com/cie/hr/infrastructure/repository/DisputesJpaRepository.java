package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.DisputesEntity;
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
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
@Repository
public interface DisputesJpaRepository extends JpaRepository<DisputesEntity, UUID> {

    List<DisputesEntity> findFirstByScorecardId(UUID scorecardId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM DisputesEntity e WHERE e.id = :id")
    Optional<DisputesEntity> findByIdForWrite(@Param("id") UUID id);
}
