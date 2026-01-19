package com.cie.hr.infrastructure.service.query;

import com.cie.hr.domain.valueobject.Formation;
import com.cie.hr.infrastructure.mapper.FormationMapper;
import com.cie.hr.infrastructure.repository.FormationJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@Service
public class FormationQuery {

    private final FormationJpaRepository formationJpaRepository;

    public FormationQuery(FormationJpaRepository formationJpaRepository) {
        this.formationJpaRepository = formationJpaRepository;
    }

    public List<Formation> readAll() {
        return formationJpaRepository.findAll().stream().map(FormationMapper::toFormationDomain).toList();
    }


}
