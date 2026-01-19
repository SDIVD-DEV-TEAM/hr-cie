package com.cie.hr.application.controller;

import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.infrastructure.service.query.StatusQuery;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/status")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Status APIs")
public class StatusController {

    private final StatusQuery statusQuery;
    private final HandleRequestResponse handleRequestResponse;

    public StatusController(StatusQuery statusQuery, HandleRequestResponse handleRequestResponse) {
        this.statusQuery = statusQuery;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    public ResponseEntity<BaseResponseEntity<Object>> retrieve() {
        return handleRequestResponse.handleRequest(statusQuery::getAllStatus);
    }

    @GetMapping("/{statusId}")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveById(@PathVariable("statusId") UUID statusId) {
        return handleRequestResponse.handleRequest(() -> statusQuery.readDetail(statusId));
    }

}
