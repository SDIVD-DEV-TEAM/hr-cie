package com.cie.hr.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cie.hr.infrastructure.entity.EmployeeEntity;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, UUID> {
    Optional<EmployeeEntity> findByEmailAndDeletedFalse(String email);

    @Query(value = """
            SELECT e.* FROM employees e
            INNER JOIN profiles p on p.id = e.profile_id
            LEFT JOIN jobs j on e.id = j.employee_id
            WHERE j.employee_id is null and e.deleted = false and p.code != 'RH'
            ORDER BY e.created_at DESC
            """, nativeQuery = true)
    List<EmployeeEntity> findAllEmployeeWithoutJobs();

    Optional<EmployeeEntity> findFirstByProfileCode(String code);

    @Query(value = "SELECT e.* FROM jobs j RIGHT JOIN employees e on e.id = j.employee_id WHERE e.deleted = false ORDER BY e.created_at DESC", nativeQuery = true)
    List<EmployeeEntity> findAllEmployeeJobs();

    Optional<EmployeeEntity> findFirstByEmployeeNumber(String email);

    @QueryHints({
            @QueryHint(name = "jakarta.persistence.lock.timeout", value = "30000")
    })
    @EntityGraph(attributePaths = {"profile", "job"})
    List<EmployeeEntity> findBySendAccountIdEmail(Boolean sendAccountIdEmail);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EmployeeEntity e WHERE e.id = :id")
    Optional<EmployeeEntity> findByIdForWrite(@Param("id") UUID id);

    @Query(value = "SELECT e FROM EmployeeEntity e WHERE e.email LIKE '%cie.ci'")
    List<EmployeeEntity> findByEmailDomain();

    /**
     * Find all active employees who haven't received their credentials email
     * Used by scheduled task to send missing credentials emails
     */
    @Query(value = "SELECT e FROM EmployeeEntity e WHERE e.deleted = false AND e.active = true AND (e.sendAccountIdEmail = false OR e.sendAccountIdEmail IS NULL)")
    List<EmployeeEntity> findActiveUsersWithoutCredentialsEmail();

    /**
     * Find all employees who are currently locked
     * Used by scheduled task to unlock blocked users
     */
    @Query(value = "SELECT e FROM EmployeeEntity e WHERE e.deleted = false AND e.isNotLocked = false")
    List<EmployeeEntity> findLockedUsers();
}
