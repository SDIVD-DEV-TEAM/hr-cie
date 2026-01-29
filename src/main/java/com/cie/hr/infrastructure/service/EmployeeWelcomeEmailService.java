package com.cie.hr.infrastructure.service;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;

import jakarta.mail.MessagingException;

/**
 * Service pour gérer l'envoi des emails de bienvenue aux employés
 * Permet de contrôler quand les emails sont envoyés (pas automatiquement au bootstrap)
 */
@Service
public class EmployeeWelcomeEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeWelcomeEmailService.class);

    private final EmployeeJpaRepository employeeJpaRepository;
    private final CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher;

    public EmployeeWelcomeEmailService(EmployeeJpaRepository employeeJpaRepository,
                                        CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.createEmployeeRequestMessagePublisher = createEmployeeRequestMessagePublisher;
    }

    /**
     * Récupère tous les employés dont l'email de bienvenue n'a pas été envoyé
     */
    public List<EmployeeEntity> findEmployeesWithoutWelcomeEmail() {
        return employeeJpaRepository.findBySendAccountIdEmail(false);
    }

    /**
     * Compte les employés en attente d'email de bienvenue
     */
    public long countPendingWelcomeEmails() {
        return findEmployeesWithoutWelcomeEmail().size();
    }

    /**
     * Envoie l'email de bienvenue à un employé spécifique
     * @param employeeId ID de l'employé
     * @return true si l'email a été envoyé avec succès
     */
    @Transactional
    public boolean sendWelcomeEmail(UUID employeeId) {
        Optional<EmployeeEntity> employeeOpt = employeeJpaRepository.findById(employeeId);
        
        if (employeeOpt.isEmpty()) {
            LOGGER.warn("Employé non trouvé: {}", employeeId);
            return false;
        }

        EmployeeEntity employee = employeeOpt.get();
        
        // Vérifier si l'email n'a pas déjà été envoyé
        if (Boolean.TRUE.equals(employee.getSendAccountIdEmail())) {
            LOGGER.info("Email de bienvenue déjà envoyé pour l'employé: {} {}", 
                    employee.getLastname(), employee.getFirstname());
            return false;
        }

        return sendEmailToEmployee(employee);
    }

    /**
     * Envoie les emails de bienvenue à tous les employés en attente
     * @return Résultat avec le nombre de succès et d'échecs
     */
    @Transactional
    public EmailSendResult sendAllPendingWelcomeEmails() {
        List<EmployeeEntity> pendingEmployees = findEmployeesWithoutWelcomeEmail();
        
        LOGGER.info("Envoi des emails de bienvenue à {} employés", pendingEmployees.size());
        
        int successCount = 0;
        int failureCount = 0;
        List<String> failedEmployees = new ArrayList<>();

        for (EmployeeEntity employee : pendingEmployees) {
            boolean success = sendEmailToEmployee(employee);
            if (success) {
                successCount++;
            } else {
                failureCount++;
                failedEmployees.add(employee.getEmployeeNumber() + " - " + employee.getLastname() + " " + employee.getFirstname());
            }
        }

        LOGGER.info("Envoi terminé: {} succès, {} échecs", successCount, failureCount);
        
        return new EmailSendResult(successCount, failureCount, failedEmployees);
    }

    /**
     * Envoie l'email de bienvenue à un employé et met à jour son statut
     */
    private boolean sendEmailToEmployee(EmployeeEntity employee) {
        try {
            LOGGER.info("Envoi email de bienvenue à: {} {} ({})", 
                    employee.getLastname(), employee.getFirstname(), employee.getEmail());

            var event = new CreateEmployeeEvent(
                    EmployeeMapper.toEmployeeDomain(employee), 
                    ZonedDateTime.now(ZoneId.of("UTC"))
            );
            createEmployeeRequestMessagePublisher.publish(event);

            // Marquer l'email comme envoyé
            employee.setSendAccountIdEmail(true);
            employeeJpaRepository.save(employee);

            LOGGER.info("Email envoyé avec succès à: {}", employee.getEmail());
            return true;

        } catch (MessagingException | UnsupportedEncodingException e) {
            LOGGER.error("Erreur lors de l'envoi de l'email à {} ({}): {}", 
                    employee.getLastname(), employee.getEmail(), e.getMessage());
            return false;
        }
    }

    /**
     * Résultat de l'envoi d'emails en masse
     */
    public record EmailSendResult(int successCount, int failureCount, List<String> failedEmployees) {
        public boolean hasFailures() {
            return failureCount > 0;
        }

        public int totalProcessed() {
            return successCount + failureCount;
        }
    }
}
