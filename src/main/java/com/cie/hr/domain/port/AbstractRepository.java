package com.cie.hr.domain.port;

import java.util.Optional;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
public interface AbstractRepository<T, D> {
    void save(T domain);

    Optional<T> findById(D id);

    void updateAndSave(T domain);
}
