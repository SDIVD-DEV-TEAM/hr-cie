package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.EmployeeDomain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public interface EmployeeRepositoryPort extends AbstractRepository<EmployeeDomain, UUID> {
    Optional<EmployeeDomain> findByEmail(String email);

    Optional<Boolean> existsById(UUID id);

    Optional<EmployeeDomain> findByEmployeeNumber(String employeeNumber);

    void saveAll(List<EmployeeDomain> entities);
}
