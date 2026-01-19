package com.cie.hr.infrastructure.service.query;


import com.cie.hr.infrastructure.mapper.OrganizationTypeMapper;
import com.cie.hr.infrastructure.repository.OrganizationTypeJpaRepository;
import com.cie.hr.infrastructure.service.viewmodel.OrganizationTypeVm;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrganizationTypeQuery {

    private final OrganizationTypeJpaRepository organizationTypeJpaRepository;

    public OrganizationTypeQuery(OrganizationTypeJpaRepository organizationTypeJpaRepository) {
        this.organizationTypeJpaRepository = organizationTypeJpaRepository;
    }

    public List<OrganizationTypeVm> readAll() {
        return organizationTypeJpaRepository.findAll().stream().map(OrganizationTypeMapper::toOrganizationVm).toList();
    }
}
