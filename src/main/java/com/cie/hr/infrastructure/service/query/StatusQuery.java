package com.cie.hr.infrastructure.service.query;

import com.cie.hr.infrastructure.mapper.StatusMapper;
import com.cie.hr.infrastructure.repository.StatusJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.StatusVm;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StatusQuery {

    private final StatusJpaRepository statusJpaRepository;

    public StatusQuery(StatusJpaRepository statusJpaRepository) {
        this.statusJpaRepository = statusJpaRepository;
    }

    public List<StatusVm> getAllStatus() {
        return statusJpaRepository.findAll().stream().map(StatusMapper::toStatusVm).toList();
    }

    public Optional<StatusVm> readDetail(UUID id) {
        return statusJpaRepository.findById(id).map(StatusMapper::toStatusVm);
    }
}
