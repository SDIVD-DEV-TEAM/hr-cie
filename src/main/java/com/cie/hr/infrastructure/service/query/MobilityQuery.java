package com.cie.hr.infrastructure.service.query;

import com.cie.hr.domain.valueobject.Mobility;
import com.cie.hr.infrastructure.mapper.MobilityMapper;
import com.cie.hr.infrastructure.repository.MobilityJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Service
public class MobilityQuery {

    private final MobilityJpaRepository mobilityJpaRepository;

    public MobilityQuery(MobilityJpaRepository mobilityJpaRepository) {
        this.mobilityJpaRepository = mobilityJpaRepository;
    }

    public List<Mobility> readAll() {
        return mobilityJpaRepository.findAll().stream().map(MobilityMapper::toMobilityDomain).toList();
    }
}
