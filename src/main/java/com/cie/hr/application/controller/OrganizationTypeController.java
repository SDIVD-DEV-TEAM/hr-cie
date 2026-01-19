package com.cie.hr.application.controller;

import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.infrastructure.service.query.OrganizationTypeQuery;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/organisation")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Organization Type APIs")
public class OrganizationTypeController {

    private final OrganizationTypeQuery organizationTypeQuery;
    private final HandleRequestResponse handleRequestResponse;

    public OrganizationTypeController(OrganizationTypeQuery organizationTypeQuery, HandleRequestResponse handleRequestResponse) {
        this.organizationTypeQuery = organizationTypeQuery;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping("/organization-type")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveOrganization() {
        return handleRequestResponse.handleRequest(organizationTypeQuery::readAll);
    }
}
