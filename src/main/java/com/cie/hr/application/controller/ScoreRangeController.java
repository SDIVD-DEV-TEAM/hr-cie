package com.cie.hr.application.controller;

import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.infrastructure.service.query.ScoreRangeQuery;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexis TAMBIE
 * @created 04/07/2023
 * @project hr-cie
 */
@RestController
@RequestMapping("/api/score-range")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Score Range APIs")
public class ScoreRangeController {

    private final ScoreRangeQuery scoreRangeQuery;
    private final HandleRequestResponse handleRequestResponse;

    public ScoreRangeController(ScoreRangeQuery scoreRangeQuery, HandleRequestResponse handleRequestResponse) {
        this.scoreRangeQuery = scoreRangeQuery;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    public ResponseEntity<BaseResponseEntity<Object>> retrieve() {
        return handleRequestResponse.handleRequest(scoreRangeQuery::getAllScoreRanges);
    }
}
