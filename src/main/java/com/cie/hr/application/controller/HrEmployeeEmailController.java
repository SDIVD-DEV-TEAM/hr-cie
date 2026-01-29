package com.cie.hr.application.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cie.hr.common.adapter.BaseResponseEntity;
import com.cie.hr.common.adapter.HandleRequestResponse;
import com.cie.hr.infrastructure.service.EmployeeWelcomeEmailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Contrôleur pour la gestion des emails de bienvenue RH
 * Permet de contrôler l'envoi des emails aux nouveaux employés
 * (séparé du bootstrap pour éviter l'envoi automatique)
 */
@RestController
@RequestMapping("/api/hr/employees")
@SecurityRequirement(name = "cie-hr-api")
@CrossOrigin(origins = "*")
@PreAuthorize("hasAnyAuthority('RH', 'RHU')")
@Tag(name = "HR Employee Welcome Emails APIs")
public class HrEmployeeEmailController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final EmployeeWelcomeEmailService welcomeEmailService;
    private final HandleRequestResponse handleRequestResponse;

    public HrEmployeeEmailController(EmployeeWelcomeEmailService welcomeEmailService,
                                     HandleRequestResponse handleRequestResponse) {
        this.welcomeEmailService = welcomeEmailService;
        this.handleRequestResponse = handleRequestResponse;
    }

    /**
     * Récupère le nombre d'employés en attente d'email de bienvenue
     */
    @GetMapping("/welcome-emails/pending")
    @Operation(description = "Get count of employees pending welcome email")
    ResponseEntity<BaseResponseEntity<Object>> getPendingWelcomeEmailsCount() {
        return handleRequestResponse.handleRequest(() -> {
            long count = welcomeEmailService.countPendingWelcomeEmails();
            var employees = welcomeEmailService.findEmployeesWithoutWelcomeEmail();
            
            Map<String, Object> result = new HashMap<>();
            result.put("pendingCount", count);
            result.put("employees", employees.stream().map(emp -> Map.of(
                    "id", emp.getId(),
                    "matricule", emp.getEmployeeNumber(),
                    "nom", emp.getLastname(),
                    "prenom", emp.getFirstname(),
                    "email", emp.getEmail()
            )).toList());
            
            return result;
        });
    }

    /**
     * Envoie l'email de bienvenue à un employé spécifique
     */
    @PostMapping("/welcome-emails/send/{employeeId}")
    @Operation(description = "Send welcome email to a specific employee")
    ResponseEntity<BaseResponseEntity<Object>> sendWelcomeEmailToEmployee(@PathVariable UUID employeeId) {
        return handleRequestResponse.handleRequest(() -> {
            LOGGER.info("Demande d'envoi d'email de bienvenue pour l'employé: {}", employeeId);
            
            boolean success = welcomeEmailService.sendWelcomeEmail(employeeId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("employeeId", employeeId);
            result.put("success", success);
            result.put("message", success 
                    ? "Email de bienvenue envoyé avec succès" 
                    : "Impossible d'envoyer l'email (employé non trouvé ou email déjà envoyé)");
            
            return result;
        });
    }

    /**
     * Envoie les emails de bienvenue à tous les employés en attente
     * Cette route met en file d'attente tous les emails à envoyer
     */
    @PostMapping("/welcome-emails/send-all")
    @Operation(description = "Send welcome emails to all pending employees (queue)")
    ResponseEntity<BaseResponseEntity<Object>> sendAllPendingWelcomeEmails() {
        return handleRequestResponse.handleRequest(() -> {
            LOGGER.info("Demande d'envoi des emails de bienvenue à tous les employés en attente");
            
            EmployeeWelcomeEmailService.EmailSendResult result = welcomeEmailService.sendAllPendingWelcomeEmails();
            
            Map<String, Object> response = new HashMap<>();
            response.put("totalProcessed", result.totalProcessed());
            response.put("successCount", result.successCount());
            response.put("failureCount", result.failureCount());
            response.put("hasFailures", result.hasFailures());
            
            if (result.hasFailures()) {
                response.put("failedEmployees", result.failedEmployees());
            }
            
            response.put("message", String.format(
                    "Traitement terminé: %d emails envoyés avec succès, %d échecs",
                    result.successCount(), result.failureCount()
            ));
            
            return response;
        });
    }
}
