package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.ScoreNoteDistributionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScoreNoteDistributionJpaRepository extends JpaRepository<ScoreNoteDistributionEntity, UUID> {

    @Query(value = """
            SELECT s.* from score_note_distribution s
            WHERE s.min_interval <= :value AND s.max_interval >= :value AND note_distribution_id = :id
            ORDER BY min_interval
            LIMIT 1
            """, nativeQuery = true)
    Optional<ScoreNoteDistributionEntity> findFirstByValueInRangeAndNoteDistributionId(@Param("value") double value, @Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ScoreNoteDistributionEntity e WHERE e.id = :id")
    Optional<ScoreNoteDistributionEntity> findByIdForWrite(@Param("id") UUID id);
}