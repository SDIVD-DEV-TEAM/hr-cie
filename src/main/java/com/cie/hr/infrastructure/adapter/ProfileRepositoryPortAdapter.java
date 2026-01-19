package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Profile;
import com.cie.hr.domain.port.ProfileRepositoryPort;
import com.cie.hr.infrastructure.entity.ProfileEntity;
import com.cie.hr.infrastructure.mapper.ProfileMapper;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
@Transactional
@Service
public class ProfileRepositoryPortAdapter implements ProfileRepositoryPort {

    private final ProfileJpaRepository profileJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ProfileRepositoryPortAdapter(ProfileJpaRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    @Override
    public void save(Profile entity) {
        var profileEntity = ProfileMapper.toEntity(entity);
        this.profileJpaRepository.save(profileEntity);
    }

    @Override
    public Optional<Profile> findById(UUID id) {
        return this.profileJpaRepository.findById(id).map(ProfileMapper::toDomain);
    }

    @Override
    public void updateAndSave(Profile domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                ProfileEntity profileEntity = profileJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Ce profil n'existe pas"));
                ProfileMapper.updateAndSave(domain, profileEntity);
                profileJpaRepository.save(profileEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update organization after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating organization", e);
                throw e;
            }
        }
    }

    @Override
    public boolean searchIfExists(String name, String code) {
        return this.profileJpaRepository.existsByNameOrCode(name, code);
    }
}
