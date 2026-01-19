package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Status;
import com.cie.hr.infrastructure.entity.StatusEntity;
import com.cie.hr.infrastructure.service.viewmodel.StatusVm;

import java.util.Objects;

public class StatusMapper {

    public static StatusVm toStatusVm(StatusEntity status) {
        if (status == null) {
            return null;
        }
        return new StatusVm(status.getId(), status.getName(), status.getCode());
    }

    public static StatusEntity toStatusEntity(Status status) {
        if (status == null) {
            return null;
        }
        StatusEntity statusEntity = new StatusEntity(status.getName(), status.getCode());
        statusEntity.setId(status.getId());
        return statusEntity;
    }

    public static Status toStatusDomain(StatusEntity status) {
        if (status == null) {
            return null;
        }
        return Status.newBuilder()
                .id(status.getId())
                .code(status.getCode())
                .name(status.getName())
                .deleted(status.isDeleted())
                .build();
    }

    public static void updateAndSave(Status domain, StatusEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        if (!Objects.equals(domain.getDeleted(), entity.isDeleted())) {
            entity.setDeleted(domain.getDeleted());
        }
    }
}
