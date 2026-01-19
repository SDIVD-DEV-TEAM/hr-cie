package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.ScoreEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreJpaRepository extends JpaRepository<ScoreEntity, UUID> {
    Optional<ScoreEntity> findFirstByScore(float score);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ScoreEntity e WHERE e.id = :id")
    Optional<ScoreEntity> findByIdForWrite(@Param("id") UUID id);
}