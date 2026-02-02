package com.cie.hr.application.controller;

import java.util.UUID;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cie.hr.application.command.AssignJobToEmployeeCommand;
import com.cie.hr.application.command.AssignJobToEmployeeLightCommand;
import com.cie.hr.application.command.CreateJobCommand;
import com.cie.hr.application.command.DeleteJobCommand;
import com.cie.hr.application.command.UpdateJobCommand;
import com.cie.hr.application.command.UpdateJobScorecard;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.domain.usecase.JobUseCases;
import com.cie.hr.infrastructure.service.query.JobQuery;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */

@RestController
@RequestMapping("/api/job")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("isAuthenticated()")
@Tag(name = "Job APIs")
public class JobController {

    private final JobUseCases jobUseCases;
    private final JobQuery jobQuery;
    private final JobRepositoryPort jobRepositoryPort;
    private final HandleRequestResponse handleRequestResponse;

    public JobController(JobUseCases jobUseCases, JobQuery jobQuery, JobRepositoryPort jobRepositoryPort, HandleRequestResponse handleRequestResponse) {
        this.jobUseCases = jobUseCases;
        this.jobQuery = jobQuery;
        this.jobRepositoryPort = jobRepositoryPort;
        this.handleRequestResponse = handleRequestResponse;
    }

    @GetMapping
    @Operation(description = "Returns all jobs")
    public ResponseEntity<BaseResponseEntity<Object>> retrieveAll() {
        return handleRequestResponse.handleRequest(jobQuery::readAllJobs);
    }

    //Details Job
    @GetMapping("/{jobId}")
    @Operation(description = "Job's detailed view")
    public ResponseEntity<BaseResponseEntity<Object>> retrieve(@PathVariable UUID jobId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.viewDetails(jobId));
    }

    // create a job
    @PostMapping
    @Operation(description = "Create a new job")
    public ResponseEntity<BaseResponseEntity<Object>> createJob(@RequestBody CreateJobCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UUID job = command.execute(jobUseCases);
            return jobRepositoryPort.findById(job);
        });
    }

    @PutMapping("/{jobId}")
    @Operation(description = "Update a job")
    public ResponseEntity<BaseResponseEntity<Object>> updateJob(@PathVariable("jobId") UUID jobId, @RequestBody CreateJobCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            UpdateJobCommand updateJobCommand = new UpdateJobCommand(jobId, command);
            UUID job = updateJobCommand.execute(jobUseCases);
            return jobRepositoryPort.findById(job);
        });
    }

    @DeleteMapping
    @Operation(description = "delete logically a job ")
    public ResponseEntity<BaseResponseEntity<Object>> deleteJob(@RequestBody DeleteJobCommand command) {
        return handleRequestResponse.handleRequest(() -> command.execute(jobUseCases));
    }

    @PutMapping("assign-employee-to-job/{jobId}")
    @Operation(description = "Assign Job to an employee")
    public ResponseEntity<BaseResponseEntity<Object>> assignJobToEmployee(@PathVariable("jobId") UUID jobId, @RequestBody AssignJobToEmployeeLightCommand commandBody) {
        return handleRequestResponse.handleRequest(() -> {
            AssignJobToEmployeeCommand command = new AssignJobToEmployeeCommand(jobId, commandBody.employeeId());
            return command.execute(jobUseCases);
        });
    }

    @PutMapping("/{jobId}/employee/{employeeId}")
    @Operation(description = "Assign Job to an employee (Alternative Route)")
    public ResponseEntity<BaseResponseEntity<Object>> assignJobToEmployeeAlternative(@PathVariable("jobId") UUID jobId, @PathVariable("employeeId") UUID employeeId) {
        return handleRequestResponse.handleRequest(() -> {
            AssignJobToEmployeeCommand command = new AssignJobToEmployeeCommand(jobId, employeeId);
            return command.execute(jobUseCases);
        });
    }

    @GetMapping("/under-management-line/{jobId}")
    @Operation(description = "Returns all jobs with employee info which are under line management of job id passed in parameter")
    ResponseEntity<BaseResponseEntity<Object>> retrieveJobOrganizationalSubHierarchy(@PathVariable(name = "jobId") UUID jobId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.retrieveAllEmployeesUnderManagementLine(jobId));
    }

    @GetMapping("/occupied-jobs")
    @Operation(description = "Returns all occupied jobs")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAllOccupiedJobs() {
        return handleRequestResponse.handleRequest(jobQuery::readAllOccupiedJobs);
    }

    @GetMapping("/available-jobs")
    @Operation(description = "Returns all available jobs")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAllAvailableJobs() {
        return handleRequestResponse.handleRequest(jobQuery::readAllAvailableJobs);
    }

    @GetMapping("/available-job-in-organization/{organizationId}")
    @Operation(description = "Returns all jobs with employee info which are under line management of job id passed in parameter")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAllAvailableJobsInOrganization(@PathVariable(name = "organizationId") UUID organizationId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.readAllAvailableJobsInOrganization(organizationId));
    }

    @GetMapping("/jobs-in-organization/{organizationId}")
    @Operation(description = "Returns all jobs with employee info which are under line management of job id passed in parameter")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAllJobsInOrganization(@PathVariable(name = "organizationId") UUID organizationId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.readAllJobsInOrganization(organizationId));
    }

    @GetMapping("/jobs-in-organization-over-grade/{organizationId}/{gradeId}")
    @Operation(description = "Returns all jobs with employee info which are under line management of job id passed in parameter")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAllJobsInOrganizationOverGrade(@PathVariable(name = "organizationId") UUID organizationId, @PathVariable(name = "gradeId") UUID gradeId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.retrieveAllJobsForSpecificGrade(organizationId, gradeId));
    }

    @GetMapping("/available-jobs-in-pole/{poleId}")
    @Operation(description = "Return all available jobs in a given pole")
    ResponseEntity<BaseResponseEntity<Object>> retrieveAvailableJobsForPole(@PathVariable(name = "poleId") UUID poleId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.employeeFromPoleWithoutJobs(poleId));
    }

    @PutMapping("/update-job-template/{jobId}")
    @Operation(description = "Update job template")
    public ResponseEntity<BaseResponseEntity<Object>> updateJobTemplate(@PathVariable(name = "jobId") UUID jobId, @RequestParam("file") MultipartFile file) {
        return handleRequestResponse.handleRequest(() -> jobUseCases.updateJobScorecard(new UpdateJobScorecard(file, jobId)));
    }

    @GetMapping("/job-template/{jobId}")
    @Operation(description = "Get job template")
    public ResponseEntity<BaseResponseEntity<Object>> getJobTemplate(@PathVariable(name = "jobId") UUID jobId) {
        return handleRequestResponse.handleRequest(() -> jobQuery.getJobScorecard(jobId));
    }
}
