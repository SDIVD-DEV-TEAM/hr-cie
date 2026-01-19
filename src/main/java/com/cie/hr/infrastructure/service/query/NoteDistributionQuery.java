package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.mapper.NoteDistributionMapper;
import com.cie.hr.infrastructure.repository.NoteDistributionJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.NoteDistributionVm;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 26/07/2023
 * @project hr-cie
 */
@Service
public class NoteDistributionQuery {

    private final NoteDistributionJpaRepository noteDistributionJpaRepository;

    public NoteDistributionQuery(NoteDistributionJpaRepository noteDistributionJpaRepository) {
        this.noteDistributionJpaRepository = noteDistributionJpaRepository;
    }

    public List<NoteDistributionVm> readAllNotes() {
        return noteDistributionJpaRepository.findAll().stream().map(NoteDistributionMapper::toNoDistributionVm).toList();
    }
}
