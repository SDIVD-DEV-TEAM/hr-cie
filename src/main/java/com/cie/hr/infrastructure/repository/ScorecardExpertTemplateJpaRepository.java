package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.ScorecardExpertTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 10/05/2023
 * @project hr
 */
@Repository
public interface ScorecardExpertTemplateJpaRepository extends JpaRepository<ScorecardExpertTemplateEntity, UUID> {
    @Query(value = "SELECT * FROM scorecard_templates e WHERE e.type = :type and e.active = true ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<ScorecardExpertTemplateEntity> findFirstByTypeAndActiveTrue(@Param("type") Integer type);
}
