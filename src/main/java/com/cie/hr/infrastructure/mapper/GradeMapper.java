package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.Grade;
import com.cie.hr.infrastructure.entity.GradeEntity;
import com.cie.hr.infrastructure.service.viewmodel.GradeVm;

import java.util.Objects;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
public class GradeMapper {
    public static GradeVm toGradeVm(GradeEntity gradeEntity) {
        if (gradeEntity == null) {
            return null;
        }
        return new GradeVm(gradeEntity.getId(), gradeEntity.getName(), gradeEntity.getDescription(), gradeEntity.getCode());
    }

    public static GradeEntity toGradeEntity(Grade grade) {
        if (grade == null) {
            return null;
        }
        var gradeEntity = GradeEntity.builder()
                .name(grade.getName())
                .code(grade.getCode())
                .description(grade.getDescription())
                .active(grade.isActive()).build();
        gradeEntity.setId(grade.getId());

        return gradeEntity;
    }

    public static Grade toGradeDomain(GradeEntity gradeEntity) {
        if (gradeEntity == null) {
            return null;
        }
        return new Grade(gradeEntity.getId(), gradeEntity.getName(), gradeEntity.getCode(), gradeEntity.getDescription(), gradeEntity.getRank(), gradeEntity.isActive());
    }

    public static void updateAndSave(Grade domain, GradeEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getName(), entity.getName())) {
            entity.setName(domain.getName());
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        if (!Objects.equals(domain.getDescription(), entity.getDescription())) {
            entity.setDescription(domain.getDescription());
        }

        if (!Objects.equals(domain.isActive(), entity.isActive())) {
            entity.setActive(domain.isActive());
        }
    }
}
