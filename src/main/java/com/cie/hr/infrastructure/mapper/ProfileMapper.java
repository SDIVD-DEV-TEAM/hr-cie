package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Profile;
import com.cie.hr.infrastructure.entity.ProfileEntity;
import com.cie.hr.infrastructure.service.viewmodel.ProfileVm;

import java.util.Objects;

/**
 * @author Koty BLEU
 * @created 02/05/2023
 * @project hr
 */
public class ProfileMapper {
    public static ProfileEntity toEntity(Profile profile) {
        if (profile == null) {
            return null;
        }
        var result = ProfileEntity.builder()
                .name(profile.getName())
                .code(profile.getCode())
                .build();
        result.setId(profile.getId());
        return result;
    }

    public static Profile toDomain(ProfileEntity profile) {
        if (profile == null) {
            return null;
        }
        return Profile.newBuilder()
                .id(profile.getId())
                .name(profile.getName())
                .code(profile.getCode())
                .build();
    }

    public static ProfileVm toViewModel(ProfileEntity profile) {
        return new ProfileVm(profile.getId(), profile.getName());
    }

    public static void updateAndSave(Profile domain, ProfileEntity entity) {
        if (entity == null || domain == null) {
            return;
        }
        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }
        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }
    }
}
