package com.cie.hr.common.event.listeners;

import com.cie.hr.common.email.impl.EmailService;
import com.cie.hr.common.event.OnResetPasswordEvent;
import com.cie.hr.common.event.publish.OnResetPasswordRequestMessagePublisher;
import com.cie.hr.common.utils.GeneratePassword;
import com.cie.hr.domain.port.EmployeeRepositoryPort;
import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

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

    @Override
    @Async
    public void publish(OnResetPasswordEvent domainEvent) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = domainEvent.getEmployee();
        var token = GeneratePassword.get(8, true, false).toUpperCase();

        if (employeeDomain.token() != null) {
            token = employeeDomain.token();
        }

        employeeDomain.saveToken(token);
        employeeDomain.saveExpiredChangeInitializeTokenDate();

        this.employeeRepository.save(employeeDomain);

        this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Initialisation du mot de passe", token, "init_password", employeeDomain.lastname() + " " + employeeDomain.firstname());
    }
}
