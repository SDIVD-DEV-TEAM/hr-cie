package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.CampaignEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Repository
public interface CampaignJpaRepository extends JpaRepository<CampaignEntity, UUID> {
    boolean existsByEndDateGreaterThan(Date start_date);

    Optional<CampaignEntity> findFirstByStartDateLessThanEqualAndStatusCodeOrderByStartDateDesc(Date today, String code);

    Optional<CampaignEntity> findFirstByEndDateLessThanEqualAndStatusCodeNot(Date today, String code);

    List<CampaignEntity> findByEndDateLessThanEqualAndStatusCode(Date today, String code);

    Optional<CampaignEntity> findFirstByStatusCodeAndDeletedFalse(String code);

    Optional<CampaignEntity> findFirstByStatusCode(String code);

    List<CampaignEntity> findByDeletedFalseOrderByCreatedDesc();

    List<CampaignEntity> findByStatusCodeIn(List<String> codes);

    List<CampaignEntity> findAllByStatusCodeOrderByStartDateDesc(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM CampaignEntity e WHERE e.id = :id")
    Optional<CampaignEntity> findByIdForWrite(@Param("id") UUID id);

    Optional<CampaignEntity> findFirstByStatusCodeAndDeletedFalseOrderByEndDateDesc(String code);
}
