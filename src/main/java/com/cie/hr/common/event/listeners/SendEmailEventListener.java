package com.cie.hr.common.event.listeners;

import com.cie.hr.common.email.impl.EmailService;
import com.cie.hr.common.event.CreateEmployeeEvent;
import com.cie.hr.common.event.publish.CreateEmployeeRequestMessagePublisher;
import com.cie.hr.common.security.port.CustomAuthenticationManager;
import com.cie.hr.common.utils.GeneratePassword;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
@Component
public class SendEmailEventListener implements CreateEmployeeRequestMessagePublisher {

    private final EmailService emailRepositoryPort;

    private final CustomAuthenticationManager authenticatePort;

    private final EmployeeRepositoryPort employeeRepository;

    public SendEmailEventListener(EmailService emailRepositoryPort, CustomAuthenticationManager authenticatePort, EmployeeRepositoryPort employeeRepository) {
        this.emailRepositoryPort = emailRepositoryPort;
        this.authenticatePort = authenticatePort;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void publish(CreateEmployeeEvent domainEvent) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = domainEvent.getEmployee();

        var defaultPassword = GeneratePassword.generateCommonLangPassword();
        var encryptedPass = authenticatePort.encodePassword(defaultPassword);

        employeeDomain.setPassword(encryptedPass);
        employeeDomain.addNewPasswordToAStore(encryptedPass);
        this.employeeRepository.save(employeeDomain);

        this.emailRepositoryPort.sendCreateAccountEmail(employeeDomain.email(), "[Evaluation RH] - Création de compte", "create_account", defaultPassword, employeeDomain.lastname() + " " + employeeDomain.firstname());
    }
}
