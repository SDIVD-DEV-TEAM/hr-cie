package com.cie.hr.infrastructure.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Units;
import com.cie.hr.domain.port.UnitsRepositoryPort;
import com.cie.hr.infrastructure.entity.UnitsEntity;
import com.cie.hr.infrastructure.mapper.UnitsMapper;
import com.cie.hr.infrastructure.repository.UnitsJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 14/06/2023
 * @project hr-cie
 */
@Transactional
@Service
public class UnitsRepositoryAdapter implements UnitsRepositoryPort {

    private final UnitsJpaRepository unitsJpaRepository;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public UnitsRepositoryAdapter(UnitsJpaRepository unitsJpaRepository) {
        this.unitsJpaRepository = unitsJpaRepository;
    }

    @Override
    public void save(Units units) {
        UnitsEntity unitsEntity = UnitsMapper.toUnitsEntity(units);
        unitsJpaRepository.save(unitsEntity);
    }

    @Override
    public Optional<Units> findById(UUID id) {
        var unit = this.unitsJpaRepository.findById(id);
        return unit.map(UnitsMapper::toUnitsDomain);
    }

    @Override
    public void updateAndSave(Units domain) {
        int retries = 3;
        while (retries > 0) {
            try {
                UnitsEntity unitEntity = unitsJpaRepository.findByIdForWrite(domain.getId()).orElseThrow(() -> new ApplicationException("Cette unité n'existe pas"));
                UnitsMapper.updateAndSave(domain, unitEntity);
                unitsJpaRepository.save(unitEntity);
                break;
            } catch (CannotAcquireLockException e) {
                retries--;
                if (retries == 0) {
                    LOGGER.error("Cannot update unit after 3 retries", e);
                    throw e;
                }
            } catch (Exception e) {
                LOGGER.error("Error while updating unit", e);
                throw e;
            }
        }
    }

    @Override
    public boolean checkNameAlreadyExists(String name) {
        return unitsJpaRepository.existsByName(name);
    }
}
