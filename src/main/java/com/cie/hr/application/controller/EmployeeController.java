package com.cie.hr.application.controller;

import com.cie.hr.application.command.*;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.usecase.DisputesUseCases;
import com.cie.hr.domain.usecase.EmployeeUseCases;
import com.cie.hr.domain.usecase.ScorecardUseCases;
import com.cie.hr.infrastructure.service.query.DisputesQuery;
import com.cie.hr.infrastructure.service.query.EmployeeQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@RestController
@RequestMapping("/api/employee")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Employees APIs")
public class EmployeeController {

    private final EmployeeUseCases employeeUseCases;
    private final ScorecardUseCases scorecardUseCases;
    private final EmployeeQuery employeeQuery;
    private final DisputesUseCases disputesUseCases;
    private final DisputesQuery disputesQuery;
    private final HandleRequestResponse handleRequestResponse;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public EmployeeController(EmployeeUseCases employeeUseCases, ScorecardUseCases scorecardUseCases, EmployeeQuery employeeQuery, DisputesUseCases disputesUseCases, DisputesQuery disputesQuery, HandleRequestResponse handleRequestResponse) {
        this.employeeUseCases = employeeUseCases;
        this.scorecardUseCases = scorecardUseCases;
        this.employeeQuery = employeeQuery;
        this.disputesUseCases = disputesUseCases;
        this.disputesQuery = disputesQuery;
        this.handleRequestResponse = handleRequestResponse;
    }

    record messageCommand(String subject, String message, boolean isRejected) {
    }

    @PostMapping
    @Operation(description = "Create an employee")
    ResponseEntity<BaseResponseEntity<Object>> createEmployee(@RequestBody CreateEmployeeCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID employeeId;
            try {
                employeeId = command.execute(employeeUseCases);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return employeeQuery.employeeDetail(employeeId);
        });
    }

    @GetMapping
    @Operation(description = "Retrieve all employees")
    ResponseEntity<BaseResponseEntity<Object>> listEmployees() {
        return handleRequestResponse.handleRequest(employeeQuery::allEmployees);
    }

    // Update employee
    @PutMapping("/{employeeId}")
    @Operation(description = "Update an employee")
    ResponseEntity<BaseResponseEntity<Object>> updateEmployee(@PathVariable("employeeId") UUID employeeId, @RequestBody CreateEmployeeCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateEmployeeCommand updateEmployeeCommand = new UpdateEmployeeCommand(employeeId, command.lastname(), command.firstname(), command.employeeNumber(), command.profileId(), command.email(), command.accessLevel());
            var updateEmployee = updateEmployeeCommand.execute(employeeUseCases);
            return employeeQuery.employeeDetail(updateEmployee.id());
        });
    }

    @GetMapping("/without-jobs")
    @Operation(description = "Retrieve all employees without jobs")
    ResponseEntity<BaseResponseEntity<Object>> listEmployeesWithoutJobs() {
        return handleRequestResponse.handleRequest(employeeQuery::employeesWithoutJobs);
    }

    @PostMapping("/delegate-notation")
    @Operation(description = "Delegate a notation to another employee")
    ResponseEntity<BaseResponseEntity<Object>> delegateNotation(@RequestBody DelegateNotationCommand command) {
        return handleRequestResponse.handleRequest(() -> command.execute(employeeUseCases));
    }

    @PostMapping("/derogation")
    @Operation(description = "Give a derogation to an another employee")
    ResponseEntity<BaseResponseEntity<Object>> giveDerogation(@RequestBody GiveDerogationCommand command) {
        return handleRequestResponse.handleRequest(() -> command.execute(employeeUseCases));
    }

    @PostMapping("/remove-delegation")
    @Operation(description = "Remove or decline a delegation")
    ResponseEntity<BaseResponseEntity<Object>> removeDelegation(@RequestBody RemoveDelegationCommand command) {
        return handleRequestResponse.handleRequest(() -> command.execute(employeeUseCases));
    }

    @PostMapping("/remove-derogation")
    @Operation(description = "Remove a derogation")
    ResponseEntity<BaseResponseEntity<Object>> removeDerogation(@RequestBody RemoveDerogationCommand command) {
        return handleRequestResponse.handleRequest(() -> command.execute(employeeUseCases));
    }

    @GetMapping("/scorecard-performance-list/{userType}")
    @Operation(description = "display scorecard performance list for a person")
    ResponseEntity<BaseResponseEntity<Object>> showPerformanceList(@PathVariable("userType") String userType) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.getPerformanceScorecards(userType.toUpperCase().strip()));
    }

    @GetMapping("/scorecard-last-year-performance-list/{userType}/{campaignId}")
    @Operation(description = "display scorecard performance list for a person")
    ResponseEntity<BaseResponseEntity<Object>> showLastYearPerformanceList(@PathVariable("userType") String userType, @PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.getLastYearPerformanceScorecards(userType.toUpperCase().strip(), campaignId));
    }

    @GetMapping("/scorecard-details/{scorecardId}")
    @Operation(description = "display a detailed view of scorecard for a an employee regardless of grade")
    ResponseEntity<BaseResponseEntity<Object>> showScorecardDetails(@PathVariable("scorecardId") UUID scorecardId) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.getEvaluationScorecardDetails(scorecardId));
    }

    @PutMapping("/scorecard-details/{scorecardId}")
    @Operation(description = "update a detailed view of scorecard for a an employee regardless of grade")
    ResponseEntity<BaseResponseEntity<Object>> updateScorecardDetails(@PathVariable("scorecardId") UUID scorecardId, @RequestBody UpdateScorecardLightCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateScoreCardCommand scoreCardCommand = new UpdateScoreCardCommand(scorecardId, command.managerId(), command.scorecardManager(), command.scorecardExpert());
            UUID updateScorecard;
            try {
                updateScorecard = scoreCardCommand.execute(scorecardUseCases);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return employeeQuery.getEvaluationScorecardDetails(updateScorecard);
        });
    }

    @GetMapping("/scorecard-evaluation-list")
    @Operation(description = "display scorecard evaluation list for a person")
    ResponseEntity<BaseResponseEntity<Object>> showEvaluationList() {
        return handleRequestResponse.handleRequest(employeeQuery::getEvaluationScorecards);
    }

    @GetMapping("/employee-with-high-grade/{employeeId}")
    ResponseEntity<BaseResponseEntity<Object>> retrieveEmployeesWithHighGrade(@PathVariable("employeeId") UUID employeeId) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.retrieveAllEmployeeWithHighGrade(employeeId));
    }

    @PostMapping("/add-dispute-on-scorecard")
    ResponseEntity<BaseResponseEntity<Object>> createDisputeOnScorecard(@RequestBody CreateDisputeCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            try {
                UUID disputeId = disputesUseCases.createDispute(command);
                return disputesQuery.disputesDetail(disputeId).orElse(null);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @PutMapping("/add-dispute-on-scorecard/{disputeId}")
    ResponseEntity<BaseResponseEntity<Object>> updateDisputeOnScorecard(@PathVariable("disputeId") UUID disputeId, @RequestBody messageCommand message) {
        return handleRequestResponse.handleRequest(() -> {
            try {
                UpdateDisputesCommand disputesCommand = new UpdateDisputesCommand(disputeId, message.subject, message.message, message.isRejected);
                UUID dispute = disputesUseCases.updateDispute(disputesCommand);
                return disputesQuery.disputesDetail(dispute).orElse(null);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @GetMapping("/all-dispute-on-scorecard/{scorecardId}")
    ResponseEntity<BaseResponseEntity<Object>> listDisputeOnScorecard(@PathVariable("scorecardId") UUID scorecardId) {
        return handleRequestResponse.handleRequest(() -> disputesQuery.disputesDetailByScorecard(scorecardId));
    }

    @PutMapping("/close-scorecard/{scorecardId}")
    ResponseEntity<BaseResponseEntity<Object>> closeScorecard(@PathVariable("scorecardId") UUID scorecardId) {
        return handleRequestResponse.handleRequest(() -> {
            CloseScoreCardCommand closeScoreCardCommand = new CloseScoreCardCommand(scorecardId);
            UUID scorecard = scorecardUseCases.closeScorecard(closeScoreCardCommand);
            return employeeQuery.getEvaluationScorecardDetails(scorecard);
        });
    }

    @GetMapping("/last-three-years-statistics/{employeeId}")
    ResponseEntity<BaseResponseEntity<Object>> lastThreeYearsStatistics(@PathVariable("employeeId") UUID employeeId) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.lastYearEvaluationStatistics(employeeId));
    }

    @GetMapping("/collaborators-evaluation-list/{campaignId}")
    ResponseEntity<BaseResponseEntity<Object>> collaboratorsEvaluationsList(@PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.collaboratorsEvaluationScorecards(campaignId));
    }

    @GetMapping("/dashboard-stat/{campaignId}/{userType}")
    ResponseEntity<BaseResponseEntity<Object>> managerDashboardStat(@PathVariable("campaignId") UUID campaignId, @PathVariable("userType") String userType) {
        return handleRequestResponse.handleRequest(() -> employeeQuery.dashboardStat(campaignId, userType.toUpperCase().strip()));
    }

    @PatchMapping("/recreate-employee-scorecard/{employeeId}/{campaignId}")
    ResponseEntity<BaseResponseEntity<Object>> recreateEmployeeScorecard(@PathVariable("employeeId") UUID employeeId, @PathVariable("campaignId") UUID campaignId) {
        return handleRequestResponse.handleRequest(() -> {
            ReCreatedScorecardCommand command = new ReCreatedScorecardCommand(employeeId, campaignId);
            LOGGER.info("ReCreatedScorecardCommand {}", command);
            return command.execute(scorecardUseCases);
        });
    }

    @PatchMapping("/reactivate-employee/{employeeId}")
    ResponseEntity<BaseResponseEntity<Object>> reactivateEmployee(@PathVariable("employeeId") UUID employeeId) {
        return handleRequestResponse.handleRequest(() -> {
            ReactivateUserCommand command = new ReactivateUserCommand(employeeId);
            LOGGER.info("ReactivateUserCommand {}", command);
            return command.execute(employeeUseCases);
        });
    }
}
