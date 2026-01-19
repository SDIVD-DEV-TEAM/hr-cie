package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Job;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
public interface JobRepositoryPort extends AbstractRepository<Job, UUID> {
    boolean existsByCode(String code);

    Optional<Boolean> existsById(UUID id);

    Optional<UUID> findByCode(String code);

    List<Job> findByParentId(UUID id);

    List<Job> findAllJobsWithEmployees();

    Optional<Job> findByEmployeeId(UUID employeeId);

    void save(Job job);
}
