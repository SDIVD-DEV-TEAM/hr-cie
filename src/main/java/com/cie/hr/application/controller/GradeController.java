package com.cie.hr.application.controller;

import com.cie.hr.application.command.CreateGradeCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.usecase.GradeUseCases;
import com.cie.hr.infrastructure.service.query.GradeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
@RestController
@RequestMapping("/api/grade")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Grades APIs")
public class GradeController {

    private final GradeQuery gradeQuery;
    private final GradeUseCases gradeUseCases;
    private final HandleRequestResponse handleRequestResponse;

    public GradeController(GradeQuery gradeQuery, GradeUseCases gradeUseCases, HandleRequestResponse handleRequestResponse) {
        this.gradeQuery = gradeQuery;
        this.gradeUseCases = gradeUseCases;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    @Operation(description = "Displays all grades")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveGrades() {
        return handleRequestResponse.handleRequest(gradeQuery::readAll);
    }

    @PostMapping
    @Operation(description = "Create a grade")
    public ResponseEntity<BaseResponseEntity<Object>> createGrade(@RequestBody CreateGradeCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID gradeId = command.execute(gradeUseCases);
            return gradeQuery.readGradeDetail(gradeId);
        });
    }
}
