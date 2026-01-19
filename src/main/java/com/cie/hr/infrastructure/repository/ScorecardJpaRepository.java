package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.ScorecardEntity;
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
public interface ScorecardJpaRepository extends JpaRepository<ScorecardEntity, UUID> {

    List<ScorecardEntity> findAllByDeletedFalseAndAssessedIdAndCampaignStatusNameNot(UUID assessedId, String status);

    Optional<ScorecardEntity> findFirstByDeletedFalseAndAssessedIdAndCampaignId(UUID assessedId, UUID campaignId);

    List<ScorecardEntity> findAllByManagerId(UUID assessedId);

    @Query(value = """
            SELECT s.*
            FROM scorecards s
            INNER JOIN employees e on e.id = s.assessed_id
            INNER JOIN jobs j on j.employee_id = e.id
            INNER JOIN grades g on g.id = j.grade_id
            INNER JOIN status st on st.id = s.status_id
            WHERE g.code in :codes
            AND s.campaign_id = :campaign_id""", nativeQuery = true)
    List<ScorecardEntity> findAllScorecardWithCampaignAndGrade(@Param("codes") List<String> codes, @Param("campaign_id") UUID campaign_id);

    @Query(value = """
            SELECT s.*
            FROM scorecards s
            INNER JOIN employees e on e.id = s.assessed_id
            INNER JOIN jobs j on j.employee_id = e.id
            INNER JOIN status st on st.id = s.status_id
            WHERE j.organization_id = :organization
            AND st.code = '2'
            """, nativeQuery = true)
    List<ScorecardEntity> findAllOrganizationScorecard(@Param("organization") UUID campaign_id);

    List<ScorecardEntity> findByDeletedFalseAndCampaignId(UUID campaignId);

    Optional<ScorecardEntity> findByDeletedFalseAndAssessedIdAndCampaignId(UUID id, UUID campaignId);

    @Query(value = """
            SELECT s.* FROM scorecards s
            INNER JOIN status st on st.id = s.status_id
            WHERE st.name = :name AND s.evaluated_at <= NOW() - INTERVAL '7 days'
            """, nativeQuery = true)
    List<ScorecardEntity> findByStatusNameAndEndDateDaysBefore(@Param("name") String name);

    List<ScorecardEntity> findAllByDeletedFalseAndCampaignIdAndStatusNameIn(UUID campaignId, List<String> names);

    List<ScorecardEntity> findFirst3ByAssessedIdOrderByCampaignEndDateDesc(UUID id);

    List<ScorecardEntity> findByDeletedFalseAndAssessedIdInAndCampaignId(List<UUID> ids, UUID campaignId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM ScorecardEntity e WHERE e.id = :id")
    Optional<ScorecardEntity> findByIdForWrite(@Param("id") UUID id);
}
