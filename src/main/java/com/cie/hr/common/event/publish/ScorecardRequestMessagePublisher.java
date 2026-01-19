package com.cie.hr.common.event.publish;

import com.cie.hr.common.domain.event.DomainEvent;
import com.cie.hr.common.event.ScorecardEvent;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis TAMBIE
 * @created 24/06/2023
 * @project hr-cie
 */
public interface ScorecardRequestMessagePublisher extends DomainEvent<ScorecardEvent> {
    void publishWithParam(boolean is_manager, ScorecardEvent scorecardEvent, String employeeName, boolean is_rejected) throws MessagingException, UnsupportedEncodingException;

    void publishManagerWithParam(ScorecardEvent scorecardEvent, String employeeName) throws MessagingException, UnsupportedEncodingException;

    void publishOnClose(ScorecardEvent scorecardEvent) throws MessagingException, UnsupportedEncodingException;

    void publishOnNotEvaluated(ScorecardEvent scorecardEvent, String days) throws MessagingException, UnsupportedEncodingException;
}
