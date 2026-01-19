package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.GradeEntity;
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
 * @created 02/05/2023
 * @project hr
 */
@Repository
public interface GradeJpaRepository extends JpaRepository<GradeEntity, UUID> {
    boolean existsByName(String name);

    GradeEntity findByCode(String code);

    Optional<List<GradeEntity>> findByActiveTrueOrderByCreatedDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM GradeEntity e WHERE e.id = :id")
    Optional<GradeEntity> findByIdForWrite(@Param("id") UUID id);
}
