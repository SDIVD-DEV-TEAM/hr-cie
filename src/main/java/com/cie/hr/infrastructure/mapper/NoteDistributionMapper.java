package com.cie.hr.infrastructure.mapper;

import com.cie.hr.domain.entity.NoteDistribution;
import com.cie.hr.infrastructure.entity.NoteDistributionEntity;
import com.cie.hr.infrastructure.service.viewmodel.NoteDistributionVm;

import java.util.Objects;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
public class NoteDistributionMapper {

    public static NoteDistributionEntity toEntity(NoteDistribution noteDistribution) {
        if (noteDistribution == null) {
            return null;
        }

        return NoteDistributionEntity.builder()
                .id(noteDistribution.getId())
                .way(noteDistribution.getWay())
                .allOrNothing(noteDistribution.isAllOrNothing())
                .code(noteDistribution.getCode())
                .description(noteDistribution.getDescription())
                .mayExceed(noteDistribution.isMayExceed())
                .category(noteDistribution.getCategory())
                .maxWhenMayExceed(noteDistribution.isMaxWhenMayExceed())
                .build();
    }

    public static NoteDistribution toDomain(NoteDistributionEntity noteDistribution) {
        if (noteDistribution == null) {
            return null;
        }

        return NoteDistribution.newBuilder()
                .id(noteDistribution.getId())
                .allOrNothing(noteDistribution.isAllOrNothing())
                .category(noteDistribution.getCategory())
                .maxWhenMayExceed(noteDistribution.getMaxWhenMayExceed())
                .description(noteDistribution.getDescription())
                .mayExceed(noteDistribution.isMayExceed())
                .way(noteDistribution.getWay())
                .code(noteDistribution.getCode())
                .build();
    }

    public static NoteDistributionVm toNoDistributionVm(NoteDistributionEntity noteDistribution) {
        if (noteDistribution == null) {
            return null;
        }
        return new NoteDistributionVm(
                noteDistribution.getId(),
                noteDistribution.getCode(),
                noteDistribution.getDescription(),
                noteDistribution.getCategory(),
                noteDistribution.getWay(),
                noteDistribution.isMayExceed(),
                noteDistribution.getMaxWhenMayExceed(),
                noteDistribution.isAllOrNothing());
    }

    public static void updateAndSave(NoteDistribution domain, NoteDistributionEntity entity) {
        if (entity == null || domain == null) {
            return;
        }

        if (!Objects.equals(domain.getCode(), entity.getCode())) {
            entity.setCode(domain.getCode());
        }

        if (!Objects.equals(domain.getDescription(), entity.getDescription())) {
            entity.setDescription(domain.getDescription());
        }

        if (!Objects.equals(domain.getCategory(), entity.getCategory())) {
            entity.setCategory(domain.getCategory());
        }

        if (!Objects.equals(domain.getWay(), entity.getWay())) {
            entity.setWay(domain.getWay());
        }

        if (!Objects.equals(domain.isMayExceed(), entity.isMayExceed())) {
            entity.setMayExceed(domain.isMayExceed());
        }

        if (!Objects.equals(domain.getMaxWhenMayExceed(), entity.getMaxWhenMayExceed())) {
            entity.setMaxWhenMayExceed(domain.getMaxWhenMayExceed());
        }

        if (!Objects.equals(domain.isAllOrNothing(), entity.isAllOrNothing())) {
            entity.setAllOrNothing(domain.isAllOrNothing());
        }
    }
}
