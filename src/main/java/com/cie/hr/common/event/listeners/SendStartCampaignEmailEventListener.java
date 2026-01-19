package com.cie.hr.common.event.listeners;

import com.cie.hr.common.email.impl.EmailService;
import com.cie.hr.common.event.StartCampaignEvent;
import com.cie.hr.common.event.publish.StartCampaignRequestMessagePublisher;
import com.cie.hr.common.exception.ApplicationException;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 24/06/2023
 * @project hr-cie
 */
@Component
public class SendStartCampaignEmailEventListener implements StartCampaignRequestMessagePublisher {

    private final EmailService emailRepositoryPort;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public SendStartCampaignEmailEventListener(EmailService emailRepositoryPort) {
        this.emailRepositoryPort = emailRepositoryPort;
    }

    @Override
    public void publishListWithParam(Date end_date, List<StartCampaignEvent> startCampaignEventList) throws MessagingException, UnsupportedEncodingException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String displayDate = format.format(end_date);
        startCampaignEventList.forEach(employee -> {
            var employeeDomain = employee.getEmployee();
            try {
                this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Lancement d'une campagne d'évaluation", displayDate, "campaign_start", employeeDomain.lastname() + " " + employeeDomain.firstname());
            } catch (MessagingException | UnsupportedEncodingException e) {
                LOGGER.error("Error when sending email to {}", employeeDomain.email(), e);
                throw new ApplicationException(e.getMessage());
            }
        });
    }
}
