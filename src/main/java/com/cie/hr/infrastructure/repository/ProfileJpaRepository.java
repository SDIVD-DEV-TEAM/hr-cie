package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.ProfileEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Repository
public interface ProfileJpaRepository extends JpaRepository<ProfileEntity, UUID> {
    Optional<ProfileEntity> findFirstByCode(String code);

    boolean existsByNameOrCode(String name, String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ProfileEntity e WHERE e.id = :id")
    Optional<ProfileEntity> findByIdForWrite(@Param("id") UUID id);
}
