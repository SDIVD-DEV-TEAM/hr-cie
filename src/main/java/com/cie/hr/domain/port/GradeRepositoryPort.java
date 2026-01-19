package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Grade;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public interface GradeRepositoryPort extends AbstractRepository<Grade, UUID> {

    Grade findByCode(String code);

    boolean checkNameAlreadyExists(String name);
}
