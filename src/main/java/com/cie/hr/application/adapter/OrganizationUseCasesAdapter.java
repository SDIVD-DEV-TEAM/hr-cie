package com.cie.hr.application.adapter;

import com.cie.hr.application.command.CreateOrganizationCommand;
import com.cie.hr.application.command.UpdateOrganizationCommand;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.domain.entity.Organization;
import com.cie.hr.domain.entity.OrganizationType;
import com.cie.hr.domain.port.OrganizationRepositoryPort;
import com.cie.hr.domain.port.OrganizationTypeRepositoryPort;
import com.cie.hr.domain.usecase.OrganizationUseCases;
import com.fasterxml.uuid.Generators;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrganizationUseCasesAdapter implements OrganizationUseCases {

    private final OrganizationRepositoryPort organizationRepositoryPort;
    private final OrganizationTypeRepositoryPort organizationTypeRepositoryPort;

    public OrganizationUseCasesAdapter(OrganizationRepositoryPort organizationRepositoryPort, OrganizationTypeRepositoryPort organizationTypeRepositoryPort) {
        this.organizationRepositoryPort = organizationRepositoryPort;
        this.organizationTypeRepositoryPort = organizationTypeRepositoryPort;
    }
    
    public Organization createOrganization(CreateOrganizationCommand command) {
        command.checkValidity();

        String shortCode = command.shortCode().isEmpty() ? null: command.shortCode().strip().toUpperCase();
        var organizationDomain = new Organization(
                Generators.timeBasedEpochGenerator().generate(),
                command.name().strip(),
                command.code().strip().toUpperCase(),
                getOrganization(command.parentId()),
                getOrganizationType(command.typeId()),
                command.costCenter().strip(),
                shortCode
        );

        organizationDomain.checksBusinessRules(organizationRepositoryPort, organizationTypeRepositoryPort);

        // save all entities
        organizationRepositoryPort.save(organizationDomain);
        return organizationDomain;
    }

    @Override
    public Organization updateOrganization(UpdateOrganizationCommand command) {
        command.checkValidity();

        //Current Organization
        Organization organization = getOrganization(command.id());

        if (organization == null) {
            throw new ApplicationException("L'organisation n'existe pas");
        }
        String targetCode = command.createOrganizationCommand().code().strip().toUpperCase();
        Organization targetOrganization = getOrganizationByCode(targetCode);
        if (targetOrganization != null && !targetOrganization.getId().equals(organization.getId())) {
            throw new ApplicationException("Le code de l'organisation existe déjà");
        }

        Organization parent = command.createOrganizationCommand().parentId() == null ? null : getOrganization(command.createOrganizationCommand().parentId());
        String shortCode = command.createOrganizationCommand().shortCode().isEmpty() ? null: command.createOrganizationCommand().shortCode().strip().toUpperCase();

        organization.setName(command.createOrganizationCommand().name().strip());
        organization.setCode(targetCode);
        organization.setParent(parent);
        organization.setType(getOrganizationType(command.createOrganizationCommand().typeId()));
        organization.setCostCenter(command.createOrganizationCommand().costCenter().strip());
        organization.setShortCode(shortCode);
        organizationRepositoryPort.updateAndSave(organization);

        return organization;
    }


    private Organization getOrganization(UUID Id) {
        return organizationRepositoryPort.findById(Id).orElse(null);
    }

    private OrganizationType getOrganizationType(UUID typeId) {
        return organizationTypeRepositoryPort.findById(typeId).orElseThrow(() -> new ApplicationException("Le type d'organisation n'existe pas"));
    }

    private Organization getOrganizationByCode(String code) {
        return organizationRepositoryPort.findByCode(code).orElse(null);
    }
}
