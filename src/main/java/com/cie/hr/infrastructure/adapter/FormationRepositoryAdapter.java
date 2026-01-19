package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.port.FormationRepositoryPort;
import com.cie.hr.domain.valueobject.Formation;
import com.cie.hr.infrastructure.entity.FormationEntity;
import com.cie.hr.infrastructure.mapper.FormationMapper;
import com.cie.hr.infrastructure.repository.FormationJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Transactional
@Service
public class FormationRepositoryAdapter implements FormationRepositoryPort {

    private final FormationJpaRepository formationJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public FormationRepositoryAdapter(FormationJpaRepository formationJpaRepository) {
        this.formationJpaRepository = formationJpaRepository;
    }

    @Override
    public void save(Formation formation) {
        FormationEntity formationEntity = FormationMapper.toFormationEntity(formation);
        formationJpaRepository.save(formationEntity);
    }

    @Override
    public Optional<Formation> findById(UUID id) {
        return formationJpaRepository.findById(id).map(FormationMapper::toFormationDomain);
    }

    @Override
    public void updateAndSave(Formation domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                FormationEntity formationEntity = formationJpaRepository.findByIdForWrite(domain.id()).orElseThrow(() -> new ApplicationException("Cet employé n'existe pas"));
                FormationMapper.updateAndSave(domain, formationEntity);
                formationJpaRepository.save(formationEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update formation after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating formation", e);
                throw e;
            }
        }
    }
}
