package com.cie.hr.domain.port;

import com.cie.hr.domain.entity.Profile;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */

public interface ProfileRepositoryPort extends AbstractRepository<Profile, UUID> {
    boolean searchIfExists(String name, String code);
}
