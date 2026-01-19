package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteDistributionJpaRepository extends JpaRepository<NoteDistributionEntity, UUID> {
    Optional<NoteDistributionEntity> findFirstByCode(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM NoteDistributionEntity e WHERE e.id = :id")
    Optional<NoteDistributionEntity> findByIdForWrite(@Param("id") UUID id);
}