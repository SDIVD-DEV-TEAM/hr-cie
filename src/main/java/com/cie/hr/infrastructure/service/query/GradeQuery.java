package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.mapper.GradeMapper;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.GradeVm;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@Service
public class GradeQuery {

    private final GradeJpaRepository gradeJpaRepository;

    public GradeQuery(GradeJpaRepository gradeJpaRepository) {
        this.gradeJpaRepository = gradeJpaRepository;
    }

    public List<GradeVm> readAll() {
        var gradeList = gradeJpaRepository.findByActiveTrueOrderByCreatedDesc();
        return gradeList.map(gradeEntities -> gradeEntities.stream().map(GradeMapper::toGradeVm).toList()).orElseGet(ArrayList::new);
    }

    public Optional<GradeVm> readGradeDetail(UUID id) {
        return gradeJpaRepository.findById(id).map(GradeMapper::toGradeVm);
    }
}
