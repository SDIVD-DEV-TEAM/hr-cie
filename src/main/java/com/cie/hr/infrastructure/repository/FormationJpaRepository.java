package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.FormationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Repository
public interface FormationJpaRepository extends JpaRepository<FormationEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM FormationEntity e WHERE e.id = :id")
    Optional<FormationEntity> findByIdForWrite(@Param("id") UUID id);
}
