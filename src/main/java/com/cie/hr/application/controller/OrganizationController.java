package com.cie.hr.application.controller;

import com.cie.hr.application.command.CreateOrganizationCommand;
import com.cie.hr.application.command.UpdateOrganizationCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.usecase.OrganizationUseCases;
import com.cie.hr.infrastructure.service.query.OrganizationQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */

@RestController
@RequestMapping("/api/organisation")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Organization APIs")
public class OrganizationController {

    private final OrganizationQuery organizationQuery;
    private final OrganizationUseCases organizationUseCases;
    private final HandleRequestResponse handleRequestResponse;

    public OrganizationController(OrganizationQuery organizationQuery, OrganizationUseCases organizationUseCases, HandleRequestResponse handleRequestResponse) {
        this.organizationQuery = organizationQuery;
        this.organizationUseCases = organizationUseCases;
        this.handleRequestResponse = handleRequestResponse;
    }


    @GetMapping("/pole")
    @Operation(description = "return a list of all poles")
    public ResponseEntity<BaseResponseEntity<Object>> retrievePoles() {
        return handleRequestResponse.handleRequest(organizationQuery::readAllPole);
    }

    @GetMapping("/pole/{poleId}")
    @Operation(description = "return a detailed view of a pole")
    public ResponseEntity<BaseResponseEntity<Object>> retrievePole(@PathVariable("poleId") UUID poleId) {
        return handleRequestResponse.handleRequest(() -> organizationQuery.organizationById(poleId));
    }

    @GetMapping("/direction")
    @Operation(description = "return a list of all directions")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveDirections() {
        return handleRequestResponse.handleRequest(organizationQuery::organizationByDirection);
    }

    @GetMapping("/direction/{directionId}")
    @Operation(description = "return a detailed view of a direction")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveDirection(@PathVariable("directionId") UUID directionId) {
        return handleRequestResponse.handleRequest(() -> organizationQuery.organizationHierarchicById(directionId));
    }

    @GetMapping("/organization-by-grade/{gradeId}")
    @Operation(description = "return a detailed view of a direction")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveOrganizationByGrade(@PathVariable("gradeId") UUID gradeId) {
        return handleRequestResponse.handleRequest(() -> organizationQuery.organizationByGrade(gradeId));
    }

    @GetMapping("/central-direction")
    @Operation(description = "return a list of all central directions")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveCentralDirections() {
        return handleRequestResponse.handleRequest(organizationQuery::organizationByDirectionCentral);
    }

    @GetMapping("/central-direction/{centralDirectionId}")
    @Operation(description = "return a detailed view of a central direction")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveCentralDirection(@PathVariable("centralDirectionId") UUID centralDirectionId) {
        return handleRequestResponse.handleRequest(() -> organizationQuery.organizationById(centralDirectionId));
    }

    @GetMapping("/direction-adjointe")
    @Operation(description = "return a list of all directions adjointe")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveDirectionsAdjointe() {
        return handleRequestResponse.handleRequest(organizationQuery::organizationByDirectionAdjointe);
    }

    //create organization
    @PostMapping
    @Operation(description = "Create an organization")
    public ResponseEntity<BaseResponseEntity<Object>> createOrganization(@RequestBody CreateOrganizationCommand commandOrganization) {
        return handleRequestResponse.handleRequest(() -> commandOrganization.execute(organizationUseCases));
    }

    @PutMapping("/{organizationId}")
    @Operation(description = "Update an organization")
    public ResponseEntity<BaseResponseEntity<Object>> updateOrganization(@RequestBody CreateOrganizationCommand commandOrganization, @PathVariable("organizationId") UUID organizationId) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateOrganizationCommand updateOrganizationCommand = new UpdateOrganizationCommand(organizationId, commandOrganization);
            return updateOrganizationCommand.execute(organizationUseCases);
        });
    }

    @GetMapping
    @Operation(description = "Retrieve a list of all organizations")
    public ResponseEntity<BaseResponseEntity<Object>> readAll() {
        return handleRequestResponse.handleRequest(organizationQuery::getAllOrganizations);
    }

    //organization read details
    @GetMapping("/{organizationId}")
    @Operation(description = "Return a detailed view of an organization")
    public ResponseEntity<BaseResponseEntity<Object>> readDetails(@PathVariable("organizationId") UUID organizationId) {
        return handleRequestResponse.handleRequest(() -> organizationQuery.readCompleteViewDetails(organizationId));
    }

}
