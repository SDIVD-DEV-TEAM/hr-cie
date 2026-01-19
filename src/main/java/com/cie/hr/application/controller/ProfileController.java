package com.cie.hr.application.controller;

import com.cie.hr.application.command.CreateProfileCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.usecase.ProfileUseCases;
import com.cie.hr.infrastructure.service.query.ProfileQuery;
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
@RequestMapping("/api/profile")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Profile APIs")
public class ProfileController {

    private final ProfileQuery profileQuery;
    private final ProfileUseCases profileUseCases;
    private final HandleRequestResponse handleRequestResponse;

    public ProfileController(ProfileQuery profileQuery, ProfileUseCases profileUseCases, HandleRequestResponse handleRequestResponse) {
        this.profileQuery = profileQuery;
        this.profileUseCases = profileUseCases;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    @Operation(description = "Displays a list of all profiles")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveAll() {
        return handleRequestResponse.handleRequest(profileQuery::getAllProfiles);
    }

    @PostMapping
    @Operation(description = "create a new profile")
    public ResponseEntity<BaseResponseEntity<Object>> createProfile(@RequestBody CreateProfileCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID profileId = command.execute(profileUseCases);
            return profileQuery.readProfileDetail(profileId);
        });
    }

}
