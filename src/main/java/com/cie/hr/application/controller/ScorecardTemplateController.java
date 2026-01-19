package com.cie.hr.application.controller;

import com.cie.hr.application.command.UpdateScorecardExpertTemplateCommand;
import com.cie.hr.application.command.UpdateScorecardManagerTemplateCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.port.ScoreCardRepositoryPort;
import com.cie.hr.domain.usecase.ScorecardTemplateUseCases;
import com.cie.hr.infrastructure.service.query.ScorecardTemplateQuery;
import com.cie.hr.infrastructure.service.query.UnitsQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author Koty BLEU
 * @created 08/05/2023
 * @project hr
 */
@RestController
@RequestMapping("/api/scorecard-template")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Scorecard Template APIs")
public class ScorecardTemplateController {

    private final ScorecardTemplateQuery scorecardTemplateQuery;
    private final ScorecardTemplateUseCases scorecardTemplateUseCases;
    private final ScoreCardRepositoryPort scoreCardRepositoryPort;
    private final UnitsQuery unitsQuery;
    private final HandleRequestResponse handleRequestResponse;

    public ScorecardTemplateController(ScorecardTemplateQuery scorecardTemplateQuery, ScorecardTemplateUseCases scorecardTemplateUseCases, ScoreCardRepositoryPort scoreCardRepositoryPort, UnitsQuery unitsQuery, HandleRequestResponse handleRequestResponse) {
        this.scorecardTemplateQuery = scorecardTemplateQuery;
        this.scorecardTemplateUseCases = scorecardTemplateUseCases;
        this.scoreCardRepositoryPort = scoreCardRepositoryPort;
        this.unitsQuery = unitsQuery;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    @Operation(description = "Displays all scorecard templates")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveAll() {
        return handleRequestResponse.handleRequest(scorecardTemplateQuery::readAll);
    }

    @GetMapping("/view-details-template-for-manager/{scorecardTemplateId}")
    @Operation(description = "Detailed view of scorecard manager template")
    public ResponseEntity<BaseResponseEntity<Object>> viewDetailsManagerForm(@PathVariable UUID scorecardTemplateId) {
        return handleRequestResponse.handleRequest(() -> scorecardTemplateQuery.viewDetailsManagerForm(scorecardTemplateId));
    }

    @GetMapping("/view-details-template-for-expert/{scorecardTemplateId}")
    @Operation(description = "Detailed view of scorecard expert and advisor template")
    public ResponseEntity<BaseResponseEntity<Object>> viewDetailsExpertForm(@PathVariable UUID scorecardTemplateId) {
        return handleRequestResponse.handleRequest(() -> scorecardTemplateQuery.viewDetailsExpertForm(scorecardTemplateId));
    }

    @PutMapping("/form-manager")
    @Operation(description = "Update scorecard manager template")
    public ResponseEntity<BaseResponseEntity<Object>> patchScorecardManagerTemplate(@RequestBody UpdateScorecardManagerTemplateCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID scoreCardId = command.execute(scorecardTemplateUseCases);
            return scoreCardRepositoryPort.findById(scoreCardId);
        });
    }

    @PutMapping("/form-expert")
    @Operation(description = "Update scorecard expert and advisor template")
    public ResponseEntity<BaseResponseEntity<Object>> createScorecardExpertTemplate(@RequestBody UpdateScorecardExpertTemplateCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID scoreCardId = command.execute(scorecardTemplateUseCases);
            return scoreCardRepositoryPort.findById(scoreCardId);
        });

    }

    @GetMapping("/export-to-excel")
    @Operation(description = "Export scorecard template to Excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            byte[] data = unitsQuery.exportToExcel();
            return createExcelResponse(data);
        } catch (Exception ex) {
            String errorMessage = "Failed to generate campaign template.";
            return createErrorResponse(errorMessage);
        }
    }

    private ResponseEntity<byte[]> createExcelResponse(byte[] data) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=template_contrat_objectifs.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    private ResponseEntity<byte[]> createErrorResponse(String errorMessage) {
        HttpHeaders errorHeaders = new HttpHeaders();
        errorHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorMessage.getBytes(StandardCharsets.UTF_8), errorHeaders, BAD_REQUEST);
    }
}
