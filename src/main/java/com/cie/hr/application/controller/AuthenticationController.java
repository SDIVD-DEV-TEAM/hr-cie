package com.cie.hr.application.controller;

import com.cie.hr.application.command.AuthenticationCommand;
import com.cie.hr.application.command.ForgotPasswordCommand;
import com.cie.hr.application.command.ResetNewPasswordCommand;
import com.cie.hr.application.command.ResetPasswordCommand;
import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.DomainException;
import com.cie.hr.common.exception.InfrastructureException;
import com.cie.hr.common.security.model.CustomUser;
import com.cie.hr.common.security.utility.JWTTokenProvider;
import com.cie.hr.domain.entity.EmployeeDomain;
import com.cie.hr.domain.entity.Job;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import com.cie.hr.domain.port.JobRepositoryPort;
import com.cie.hr.domain.usecase.EmployeeUseCases;
import com.cie.hr.infrastructure.service.viewmodel.LoginEmployee;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.cie.hr.common.constant.Constant.HTTP_MESSAGE_OK;
import static com.cie.hr.common.constant.Constant.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Koty BLEU
 * @created 01/05/2023
 * @project hr
 */
@RestController
@RequestMapping("/api/authentication")
@CrossOrigin(origins = "*")
@Tag(name = "Authentification and Token APIs")
public class AuthenticationController {

    private final EmployeeUseCases employeeUseCases;

    private final EmployeeRepositoryPort employeeRepository;

    private final JobRepositoryPort jobRepositoryPort;

    private final HandleRequestResponse handleRequestResponse;

    private final JWTTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public AuthenticationController(EmployeeUseCases employeeUseCases, EmployeeRepositoryPort employeeRepository, JobRepositoryPort jobRepositoryPort, HandleRequestResponse handleRequestResponse, JWTTokenProvider jwtTokenProvider) {
        this.employeeUseCases = employeeUseCases;
        this.employeeRepository = employeeRepository;
        this.jobRepositoryPort = jobRepositoryPort;
        this.handleRequestResponse = handleRequestResponse;
        this.jwtTokenProvider = jwtTokenProvider;
        objectMapper = new ObjectMapper();
    }

    @PostMapping("/sign-in")
    @Operation(description = "Login user")
    public ResponseEntity<BaseResponseEntity<Object>> authentication(@RequestBody @Valid AuthenticationCommand command) {
        try {
            var loginEmployee = command.execute(employeeUseCases);

            if (loginEmployee == null) {
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.UNAUTHORIZED.value(), "Login ou mot de passe incorrect", true, null, null), HttpStatus.UNAUTHORIZED);
            } else {
                var loginUser = EmployeeDomain.newBuilder()
                        .email(loginEmployee.email())
                        .password(loginEmployee.password())
                        .profile(loginEmployee.profile())
                        .isNotLocked(true)
                        .active(loginEmployee.isActive())
                        .build();
                loginUser.setId(loginEmployee.getId());
                CustomUser employeePrincipal = new CustomUser(loginUser);
                boolean isFirstConnexion = loginEmployee.isFirstConnect();

                var job = jobRepositoryPort.findByEmployeeId(loginEmployee.id());
                boolean hasEvaluation = false;
                boolean hasPerformances = false;
                var employeeJobTitle = "";
                if (job.isPresent()) {
                    hasEvaluation = true;
                    hasPerformances = true;
                    Job employeeJob = job.get();
                    if (employeeJob.getGrade().getCode().equals("DG")) {
                        hasPerformances = false;
                    }
                    if (employeeJob.getGrade().getCode().equals("SD") || employeeJob.getGrade().getCode().equals("DR")) {
                        hasEvaluation = false;
                    }
                    employeeJobTitle = employeeJob.getGrade().getName();
                }
                LoginEmployee employeeVm;
                if (!isFirstConnexion) {
                    employeeVm = LoginEmployee.builder()
                            .employeeNumber(loginEmployee.employeeNumber())
                            .email(loginEmployee.email())
                            .id(loginEmployee.getId().getValue())
                            .lastName(loginEmployee.lastname())
                            .firstName(loginEmployee.firstname())
                            .isActive(loginEmployee.isActive())
                            .hasEvaluation(hasEvaluation)
                            .hasPerformances(hasPerformances)
                            .jobTitle(employeeJobTitle)
                            .build();
                } else {
                    employeeVm = LoginEmployee.builder()
                            .email(loginEmployee.email())
                            .firstConnexion(true)
                            .isActive(loginEmployee.isActive())
                            .id(loginEmployee.getId().getValue())
                            .firstName(loginEmployee.firstname())
                            .lastName(loginEmployee.lastname())
                            .hasEvaluation(hasEvaluation)
                            .hasPerformances(hasPerformances)
                            .jobTitle(employeeJobTitle)
                            .build();
                }

                String access_token = jwtTokenProvider.generateJwtToken(employeePrincipal, employeeVm);
                String refresh_token = jwtTokenProvider.generateJwtRefreshToken(employeePrincipal);

                ObjectNode response = objectMapper.createObjectNode();
                objectMapper.registerModule(new JavaTimeModule());

                response.put("access_token", access_token);
                response.put("refresh_token", refresh_token);

                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.OK.value(), HTTP_MESSAGE_OK, false, response, null), HttpStatus.OK);
            }
        } catch (ApplicationException | InfrastructureException | DomainException ex) {
            return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.FORBIDDEN.value(), ex.getMessage(), false, null, ex.getMessage()), HttpStatus.FORBIDDEN);
        } catch (ConstraintViolationException | IOException ex) {
            return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erreur survenue lors de la connexion", true, null, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{token}/password")
    @Operation(description = "Reset employee password")
    public ResponseEntity<BaseResponseEntity<Object>> resetPassword(@PathVariable("token") String token, @RequestBody @Valid ResetNewPasswordCommand command) {
        return handleRequestResponse.handleRequest(() -> {
            ResetPasswordCommand commandEntry = new ResetPasswordCommand(token, command.password(), command.email());
            try {
                return commandEntry.execute(employeeUseCases);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    @GetMapping("/refresh-token")
    @Operation(description = "Retrieve new refresh token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, InfrastructureException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
                Map<String, String> error = new HashMap<>();
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
            assert authorizationHeader != null;
            String refresh_token = authorizationHeader.substring(TOKEN_PREFIX.length());
            String username = jwtTokenProvider.getSubject(refresh_token);
            if (jwtTokenProvider.isTokenValid(username, refresh_token)) {
                Optional<EmployeeDomain> user = employeeRepository.findByEmail(username);
                if (user.isEmpty()) {
                    Map<String, String> error = new HashMap<>();
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), error);
                } else {
                    EmployeeDomain loginEmployee = user.get();

                    var loginUser = EmployeeDomain.newBuilder()
                            .email(loginEmployee.email())
                            .password(loginEmployee.password())
                            .profile(loginEmployee.profile())
                            .isNotLocked(true)
                            .active(loginEmployee.isActive())
                            .build();
                    loginUser.setId(loginEmployee.getId());
                    CustomUser employeePrincipal = new CustomUser(loginUser);

                    boolean isFirstConnexion = loginEmployee.isFirstConnect();

                    LoginEmployee employeeVm;
                    if (!isFirstConnexion) {
                        employeeVm = LoginEmployee.builder()
                                .employeeNumber(loginEmployee.employeeNumber())
                                .email(loginEmployee.email())
                                .id(loginEmployee.getId().getValue())
                                .lastName(loginEmployee.lastname())
                                .firstName(loginEmployee.firstname())
                                .isActive(loginEmployee.isActive())
                                .build();
                    } else {
                        employeeVm = LoginEmployee.builder()
                                .email(loginEmployee.email())
                                .firstName(loginEmployee.firstname())
                                .build();
                    }

                    String access_token = jwtTokenProvider.generateJwtToken(employeePrincipal, employeeVm);

                    Map<String, String> tokens = new HashMap<>();

                    tokens.put("access_token", access_token);
                    tokens.put("refresh_token", refresh_token);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
                }
            } else {
                SecurityContextHolder.clearContext();
                Map<String, String> error = new HashMap<>();
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setHeader("error", "Invalid Token");
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } catch (ConstraintViolationException | ApplicationException | InfrastructureException | DomainException |
                 IOException ex) {
            Map<String, String> error = new HashMap<>();
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(APPLICATION_JSON_VALUE);
            error.put("message", ex.getMessage());
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }
    }

    @PutMapping("/update/{email}/password/init")
    @Operation(description = "Initialize reset password")
    public ResponseEntity<BaseResponseEntity<Object>> checkEmailForForgetPassword(@PathVariable("email") String email) {
        return handleRequestResponse.handleRequest(() -> {
            ForgotPasswordCommand command = new ForgotPasswordCommand(email);
            try {
                command.execute(employeeUseCases);
            } catch (MessagingException | IOException e) {
                LOGGER.error("An error occurred while processing the request", e);
                return new ResponseEntity<>(new BaseResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return "Un mail a été envoyé à " + command.email();
        });
    }
}
