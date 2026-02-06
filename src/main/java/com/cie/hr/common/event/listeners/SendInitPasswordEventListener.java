package com.cie.hr.common.event.listeners;

import java.io.UnsupportedEncodingException;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.cie.hr.common.email.impl.EmailService;
import com.cie.hr.common.event.OnResetPasswordEvent;
import com.cie.hr.common.event.publish.OnResetPasswordRequestMessagePublisher;
import com.cie.hr.common.utils.GeneratePassword;
import com.cie.hr.domain.port.EmployeeRepositoryPort;

import jakarta.mail.MessagingException;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
@Component
public class SendInitPasswordEventListener implements OnResetPasswordRequestMessagePublisher {
    private final EmailService emailRepositoryPort;
    private final EmployeeRepositoryPort employeeRepository;

    public SendInitPasswordEventListener(EmailService emailRepositoryPort, EmployeeRepositoryPort employeeRepository) {
        this.emailRepositoryPort = emailRepositoryPort;
        this.employeeRepository = employeeRepository;
    }

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SendInitPasswordEventListener.class);

    @Override
    @Async
    public void publish(OnResetPasswordEvent domainEvent) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = domainEvent.getEmployee();
        try {
            LOGGER.info("Début traitement demande réinitialisation mot de passe pour: {}", employeeDomain.email());
            
            var token = GeneratePassword.get(8, true, false).toUpperCase();

            if (employeeDomain.token() != null) {
                token = employeeDomain.token();
            }

            employeeDomain.saveToken(token);
            employeeDomain.saveExpiredChangeInitializeTokenDate();

            this.employeeRepository.save(employeeDomain);

            this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Initialisation du mot de passe", token, "init_password", employeeDomain.lastname() + " " + employeeDomain.firstname());
            
            LOGGER.info("Email de réinitialisation envoyé avec succès à: {}", employeeDomain.email());
        } catch (Exception e) {
            LOGGER.error("ERREUR CRITIQUE: Echec envoi email réinitialisation pour {}. Raison: {}", 
                employeeDomain.email(), e.getMessage(), e);
        }
    }
}
