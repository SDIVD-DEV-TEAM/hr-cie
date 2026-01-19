package com.cie.hr.common.event.listeners;

import com.cie.hr.common.email.impl.EmailService;
import com.cie.hr.common.event.ScorecardEvent;
import com.cie.hr.common.event.publish.ScorecardRequestMessagePublisher;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis TAMBIE
 * @created 24/06/2023
 * @project hr-cie
 */
@Component
public class ScorecardEventListener implements ScorecardRequestMessagePublisher {
    private final EmailService emailRepositoryPort;

    public ScorecardEventListener(EmailService emailRepositoryPort) {
        this.emailRepositoryPort = emailRepositoryPort;
    }

    @Override
    public void publishWithParam(boolean is_manager, ScorecardEvent scorecardEvent, String employeeName, boolean is_rejected) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = scorecardEvent.getEmployee();
        if (is_manager) {
            String message = String.format("%s %s", employeeName, "a apporté une remarque sur l'évaluation que vous avez faite. Veuillez vous connecter afin de prendre connaissance des mentions apportées à l'évaluation.");
            this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Evaluation d'un collaborateur", message, "employee_evaluated", employeeDomain.lastname() + " " + employeeDomain.firstname());
        } else {
            if (is_rejected)
                this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Votre évaluation", "Votre manager a rejeté votre litige. Veuillez vous connecter afin de prendre connaissance des mentions apportées à l'évaluation.", "employee_evaluated", employeeDomain.lastname() + " " + employeeDomain.firstname());
            else
                this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Votre évaluation", "Votre manager vous a évalué. Veuillez vous connecter afin d'apprécier l'évaluation qui a été faite par votre manager.\n\n Au bout d'une semaine sans nouvelle de votre part, la fiche sera automatiquement cloturée et plus aucun recours ne sera possible.", "employee_evaluated", employeeDomain.lastname() + " " + employeeDomain.firstname());
        }
    }

    @Override
    public void publishManagerWithParam(ScorecardEvent scorecardEvent, String employeeName) throws MessagingException, UnsupportedEncodingException {
        String message = String.format("%s %s", "Merci d'avoir évalué votre collaborateur le/la nommé(e)", employeeName);
        this.emailRepositoryPort.sendEmail( scorecardEvent.getEmployee().email(), "[Evaluation RH] - Evaluation de votre collaborateur collaborateur", message, "employee_evaluated",  scorecardEvent.getEmployee().lastname() + " " +  scorecardEvent.getEmployee().firstname());
    }

    @Override
    public void publishOnClose(ScorecardEvent scorecardEvent) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = scorecardEvent.getEmployee();
        this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Votre évaluation", "Votre évaluation a été clôturée automatiquement car vous avez dépassé le délai autorisé pour formuler une remarque sur l'évaluation.", "employee_evaluated", employeeDomain.lastname() + " " + employeeDomain.firstname());
    }

    @Override
    public void publishOnNotEvaluated(ScorecardEvent scorecardEvent, String days) throws MessagingException, UnsupportedEncodingException {
        var employeeDomain = scorecardEvent.getEmployee();
        this.emailRepositoryPort.sendEmail(employeeDomain.email(), "[Evaluation RH] - Evaluation des collaborateurs", days, "days_before_close_campaign", employeeDomain.lastname() + " " + employeeDomain.firstname());
    }
}
