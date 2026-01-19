package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Grade;
import com.cie.hr.domain.port.GradeRepositoryPort;
import com.cie.hr.infrastructure.entity.GradeEntity;
import com.cie.hr.infrastructure.mapper.GradeMapper;
import com.cie.hr.infrastructure.repository.GradeJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
@Transactional
@Service
public class GradeRepositoryAdapter implements GradeRepositoryPort {

    private final GradeJpaRepository gradeJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public GradeRepositoryAdapter(GradeJpaRepository gradeJpaRepository) {
        this.gradeJpaRepository = gradeJpaRepository;
    }

    @Override
    public void save(Grade entity) {
        var gradeEntity = GradeMapper.toGradeEntity(entity);
        gradeJpaRepository.save(gradeEntity);
    }

    @Override
    public Optional<Grade> findById(UUID id) {
        return gradeJpaRepository.findById(id).map(GradeMapper::toGradeDomain);
    }

    @Override
    public void updateAndSave(Grade domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                GradeEntity gradeEntity = gradeJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cet employé n'existe pas"));
                GradeMapper.updateAndSave(domain, gradeEntity);
                gradeJpaRepository.save(gradeEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update grade after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating grade", e);
                throw e;
            }
        }
    }


    @Override
    public Grade findByCode(String code) {
        return GradeMapper.toGradeDomain(gradeJpaRepository.findByCode(code));
    }

    @Override
    public boolean checkNameAlreadyExists(String name) {
        return gradeJpaRepository.existsByName(name);
    }
}
