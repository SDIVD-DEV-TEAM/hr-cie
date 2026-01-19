package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CreateOrganizationCommand;
import com.cie.hr.application.command.UpdateOrganizationCommand;
import com.cie.hr.domain.entity.Organization;

public interface OrganizationUseCases {
    Organization createOrganization(CreateOrganizationCommand command);

    Organization updateOrganization(UpdateOrganizationCommand command);
}
