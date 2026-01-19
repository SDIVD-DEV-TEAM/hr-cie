package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Units;

import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
public interface UnitsRepositoryPort extends AbstractRepository<Units, UUID> {

        boolean checkNameAlreadyExists(String name);
}
