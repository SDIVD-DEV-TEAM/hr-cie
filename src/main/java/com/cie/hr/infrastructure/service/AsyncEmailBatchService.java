package com.cie.hr.infrastructure.service;

import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.infrastructure.entity.EmployeeEntity;
import com.cie.hr.infrastructure.mapper.EmployeeMapper;
import com.cie.hr.infrastructure.repository.EmployeeJpaRepository;

import jakarta.mail.MessagingException;

/**
 * Service pour l'envoi asynchrone d'emails par lots avec retry
 * Améliore les performances lors de l'envoi massif d'emails
 */
@Service
public class AsyncEmailBatchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncEmailBatchService.class);
    
    // Configuration des lots et retry
    private static final int BATCH_SIZE = 10;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 2000;
    private static final long BATCH_DELAY_MS = 500;

    private final EmployeeJpaRepository employeeJpaRepository;
    private final CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher;

    public AsyncEmailBatchService(EmployeeJpaRepository employeeJpaRepository,
                                   CreateEmployeeRequestMessagePublisher createEmployeeRequestMessagePublisher) {
        this.employeeJpaRepository = employeeJpaRepository;
        this.createEmployeeRequestMessagePublisher = createEmployeeRequestMessagePublisher;
    }

    /**
     * Envoie les emails d'identifiants de manière asynchrone par lots
     * @param employees Liste des employés à notifier
     * @return CompletableFuture avec le résultat de l'envoi
     */
    @Async
    public CompletableFuture<BatchEmailResult> sendCredentialsEmailsAsync(List<EmployeeEntity> employees) {
        LOGGER.info("Démarrage envoi asynchrone d'emails pour {} employés", employees.size());
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> failedEmployees = new ArrayList<>();

        // Traitement par lots
        List<List<EmployeeEntity>> batches = partition(employees, BATCH_SIZE);
        int batchNumber = 0;
        
        for (List<EmployeeEntity> batch : batches) {
            batchNumber++;
            LOGGER.info("Traitement du lot {}/{} ({} employés)", batchNumber, batches.size(), batch.size());
            
            for (EmployeeEntity employee : batch) {
                boolean success = sendEmailWithRetry(employee);
                if (success) {
                    successCount.incrementAndGet();
                } else {
                    failureCount.incrementAndGet();
                    failedEmployees.add(formatEmployeeInfo(employee));
                }
            }
            
            // Pause entre les lots pour éviter de surcharger le serveur SMTP
            if (batchNumber < batches.size()) {
                sleep(BATCH_DELAY_MS);
            }
        }

        BatchEmailResult result = new BatchEmailResult(successCount.get(), failureCount.get(), failedEmployees);
        LOGGER.info("Envoi terminé: {} succès, {} échecs", result.successCount(), result.failureCount());
        
        return CompletableFuture.completedFuture(result);
    }

    /**
     * Envoie un email avec mécanisme de retry
     */
    private boolean sendEmailWithRetry(EmployeeEntity employee) {
        int attempt = 0;
        
        while (attempt < MAX_RETRIES) {
            attempt++;
            try {
                LOGGER.debug("Tentative {}/{} pour {}", attempt, MAX_RETRIES, employee.getEmail());
                
                var event = new CreateEmployeeEvent(
                    EmployeeMapper.toEmployeeDomain(employee),
                    ZonedDateTime.now(ZoneId.of("UTC"))
                );
                createEmployeeRequestMessagePublisher.publish(event);
                
                // Marquer l'email comme envoyé
                employee.setSendAccountIdEmail(true);
                employeeJpaRepository.save(employee);
                
                LOGGER.info("Email envoyé avec succès à: {} (tentative {})", employee.getEmail(), attempt);
                return true;
                
            } catch (MessagingException | UnsupportedEncodingException e) {
                LOGGER.warn("Échec tentative {}/{} pour {}: {}", 
                        attempt, MAX_RETRIES, employee.getEmail(), e.getMessage());
                
                if (attempt < MAX_RETRIES) {
                    sleep(RETRY_DELAY_MS * attempt); // Backoff exponentiel
                }
            }
        }
        
        LOGGER.error("Échec définitif après {} tentatives pour: {}", MAX_RETRIES, employee.getEmail());
        return false;
    }

    /**
     * Envoie synchrone d'un seul email avec retry (pour utilisation ponctuelle)
     */
    public boolean sendSingleEmailWithRetry(EmployeeEntity employee) {
        return sendEmailWithRetry(employee);
    }

    /**
     * Divise une liste en sous-listes de taille maximale donnée
     */
    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return partitions;
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    private void sleep(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.debug("Sleep interrupted", e);
        }
    }

    private String formatEmployeeInfo(EmployeeEntity employee) {
        return String.format("%s - %s %s (%s)", 
                employee.getEmployeeNumber(), 
                employee.getLastname(), 
                employee.getFirstname(),
                employee.getEmail());
    }

    /**
     * Résultat de l'envoi par lots
     */
    public record BatchEmailResult(int successCount, int failureCount, List<String> failedEmployees) {
        public boolean hasFailures() {
            return failureCount > 0;
        }

        public int totalProcessed() {
            return successCount + failureCount;
        }
        
        public double successRate() {
            if (totalProcessed() == 0) return 100.0;
            return (successCount * 100.0) / totalProcessed();
        }
    }
}
