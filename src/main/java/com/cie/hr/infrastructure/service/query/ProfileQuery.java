package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.mapper.ProfileMapper;
import com.cie.hr.infrastructure.repository.ProfileJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.ProfileVm;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class ProfileQuery {

    private final ProfileJpaRepository profileJpaRepository;

    public ProfileQuery(ProfileJpaRepository profileJpaRepository) {
        this.profileJpaRepository = profileJpaRepository;
    }

    public List<ProfileVm> getAllProfiles() {
        return this.profileJpaRepository.findAll().stream()
                .map(ProfileMapper::toViewModel).toList();
    }

    public Optional<ProfileVm> readProfileDetail(UUID id) {
        return profileJpaRepository.findById(id).map(ProfileMapper::toViewModel);
    }
}
