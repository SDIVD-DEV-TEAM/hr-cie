package com.cie.hr.application.controller;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cie.hr.application.command.CloseCampaignCommand;
import com.cie.hr.application.command.CreateCampaignCommand;
import com.cie.hr.application.command.DeleteCampaignCommand;
import com.cie.hr.application.command.OpenCampaignCommand;
import com.cie.hr.application.command.UpdateCampaignCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.common.scheduler.ScheduledTasks;
import com.cie.hr.domain.usecase.CampaignUseCases;
import com.cie.hr.infrastructure.service.query.CampaignQuery;
import com.cie.hr.infrastructure.service.query.ScorecardsQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/campaign")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Campaigns APIs")
public class CampaignController {

    private final CampaignQuery campaignQuery;
    private final CampaignUseCases campaignUseCase;
    private final ScorecardsQuery scorecardsQuery;
    private final HandleRequestResponse handleRequestResponse;
    private final ScheduledTasks scheduledTasks;

    public CampaignController(CampaignQuery campaignQuery, CampaignUseCases campaignUseCase, ScorecardsQuery scorecardsQuery, HandleRequestResponse handleRequestResponse, ScheduledTasks scheduledTasks) {
        this.campaignQuery = campaignQuery;
        this.campaignUseCase = campaignUseCase;
        this.scorecardsQuery = scorecardsQuery;
        this.handleRequestResponse = handleRequestResponse;
        this.scheduledTasks = scheduledTasks;
    }

    record updateCampaign(String name, Date start_date, Date end_date) {
    }

    @GetMapping
    @Operation(description = "Retrieve all campaign")
    public ResponseEntity<BaseResponseEntity<Object>> retrieve() {
        return handleRequestResponse.handleRequest(campaignQuery::readAll);
    }

    @PostMapping
    @Operation(description = "Create a campaign")
    public ResponseEntity<BaseResponseEntity<Object>> createCampaign(@RequestBody CreateCampaignCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID createCampaign = command.execute(campaignUseCase);
            return campaignQuery.readCampaignDetail(createCampaign);
        });
    }

    @GetMapping("/in-progress")
    @Operation(description = "Return campaign in progress")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveCampaign() {
        return handleRequestResponse.handleRequest(() -> campaignQuery.readCampaignByStatusCode("1"));
    }

    @GetMapping("/closed-campaign")
    @Operation(description = "Return campaign all closed campaigns")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveClosedCampaign() {
        return handleRequestResponse.handleRequest(campaignQuery::readCampaignsByStatusClosed);
    }

    @GetMapping("/last-closed-campaign")
    @Operation(description = "Return campaign in progress")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveLastClosedCampaign() {
        return handleRequestResponse.handleRequest(() -> campaignQuery.readCampaignByStatusCode("2"));
    }

    @PutMapping("/{campaignId}")
    @Operation(description = "update a detailed view of campaign")
    ResponseEntity<BaseResponseEntity<Object>> updateCampaign(@PathVariable("campaignId") UUID campaignId, @RequestBody updateCampaign command) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateCampaignCommand campaignCommand = new UpdateCampaignCommand(campaignId, command.name, command.start_date, command.end_date);
            UUID updateCampaign = campaignCommand.execute(campaignUseCase);
            return campaignQuery.readCampaignDetail(updateCampaign);
        });
    }

    // Close campaign
    @PutMapping("/{campaignId}/close")
    @Operation(description = "Close a campaign")
    ResponseEntity<BaseResponseEntity<Object>> closeCampaign(@PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> {
            CloseCampaignCommand campaignCommand = new CloseCampaignCommand(campaignId);
            return campaignCommand.execute(campaignUseCase);
        });
    }

    @PutMapping("/{campaignId}/open")
    @Operation(description = "Close a campaign")
    ResponseEntity<BaseResponseEntity<Object>> openCampaign(@PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> {
            OpenCampaignCommand campaignCommand = new OpenCampaignCommand(campaignId);
            return campaignCommand.execute(campaignUseCase);
        });
    }

    @PostMapping("/trigger-scheduler")
    @Operation(description = "🔧 Déclencher manuellement le scheduler (change le statut des campagnes)")
    ResponseEntity<BaseResponseEntity<Object>> triggerScheduler() {
        return handleRequestResponse.handleRequest(() -> {
            scheduledTasks.scheduleTaskForCampaignStatusChanges();
            return "✅ Scheduler exécuté avec succès. Les campagnes ont été mises à jour.";
        });
    }

    @PostMapping("/sync-scorecards")
    @Operation(description = "🔄 Synchroniser les scorecards manquants - Détecte et inscrit les employés non inscrits dans la campagne active")
    ResponseEntity<BaseResponseEntity<Object>> syncMissingScorecards() {
        return handleRequestResponse.handleRequest(() -> {
            int created = campaignUseCase.syncMissingScorecards();
            return "✅ Synchronisation terminée : " + created + " scorecard(s) créé(s) pour les employés manquants.";
        });
    }

    @DeleteMapping("/{campaignId}")
    @Operation(description = "Delete specific campaign")
    ResponseEntity<BaseResponseEntity<Object>> deleteCampaign(@PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> {
            DeleteCampaignCommand campaignCommand = new DeleteCampaignCommand(campaignId);
            return campaignCommand.execute(campaignUseCase);
        });
    }

    @GetMapping("/download/{campaignId}/statistics")
    @Operation(description = "Download campaign statistics to excel")
    public ResponseEntity<byte[]> downloadCampaignStatistics(@PathVariable("campaignId") UUID campaignId) {
        try {
            var excelFile = scorecardsQuery.downloadScorecardToExcel(campaignId);
            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistique_campagne.xlsx");

            // Send the file as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (Exception ex) {
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            String errorMessage = "Failed to generate campaign statistics.";
            return new ResponseEntity<>(errorMessage.getBytes(StandardCharsets.UTF_8), errorHeaders, BAD_REQUEST);
        }
    }

    @GetMapping("/download-manager-note")
    @Operation(description = "Download employees statistics to excel")
    public ResponseEntity<byte[]> downloadManagerNote() {
        try {
            var excelFile = scorecardsQuery.downloadManagerToExcelNote();
            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=statistique_campagne_note.xlsx");

            // Send the file as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (Exception ex) {
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            String errorMessage = "Failed to generate campaign statistics.";
            return new ResponseEntity<>(errorMessage.getBytes(StandardCharsets.UTF_8), errorHeaders, BAD_REQUEST);
        }
    }

    @GetMapping("/download/{scorecardId}/pdf")
    @Operation(description = "Download employee scorecard to pdf")
    public ResponseEntity<byte[]> downloadScorecardPDF(@PathVariable("scorecardId") UUID scorecardId) {
        try {
            var pdfFile = scorecardsQuery.generateScorecardPdf(scorecardId);
            // Set headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=fiche_collaborateur.pdf");

            // Send the file as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfFile);
        } catch (Exception ex) {
            HttpHeaders errorHeaders = new HttpHeaders();
            errorHeaders.setContentType(MediaType.APPLICATION_JSON);
            String errorMessage = "Failed to generate campaign statistics.";
            return new ResponseEntity<>(errorMessage.getBytes(StandardCharsets.UTF_8), errorHeaders, BAD_REQUEST);
        }
    }
}
