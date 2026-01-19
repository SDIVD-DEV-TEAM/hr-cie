package com.cie.hr.infrastructure.repository;

import com.cie.hr.infrastructure.entity.JobEntity;
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
public interface JobJpaRepository extends JpaRepository<JobEntity, UUID> {

    boolean existsByCode(String code);

    Optional<JobEntity> findByCode(String code);

    List<JobEntity> findByParentId(UUID id);

    // find all jobs where id in
    List<JobEntity> findByOrganizationIdInAndEmployeeIdIsNull(List<UUID> ids);

    List<JobEntity> findByDeletedFalseAndParentIdAndEmployeeIdIsNotNullAndEmployeeIdNot(UUID id, UUID employeeId);

    List<JobEntity> findAllByDeletedFalseAndEmployeeIdIsNotNull();

    Optional<JobEntity> findByEmployeeId(UUID employeeId);

    List<JobEntity> findAllByDeletedFalseAndEmployeeIdIsNull();

    List<JobEntity> findByDeletedFalseOrderByCreatedDesc();

    List<JobEntity> findAllByDeletedFalseAndEmployeeIdIsNullAndOrganizationId(UUID id);

    List<JobEntity> findAllByDeletedFalseAndOrganizationId(UUID id);

    List<JobEntity> findByParentIdOrEmployeeIdIn(UUID id, List<UUID> employees);

    @Query(value = """
            SELECT j.* FROM employees e
            INNER JOIN jobs j on j.employee_id = e.id
            INNER JOIN grades g on g.id = j.grade_id
            WHERE g.rank < :rank""", nativeQuery = true)
    List<JobEntity> findByJobGradeRankLessThan(@Param("rank") int rank);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM JobEntity e WHERE e.id = :id")
    Optional<JobEntity> findByIdForWrite(@Param("id") UUID id);
}
