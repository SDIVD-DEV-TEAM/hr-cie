package com.cie.hr.common.domain.event.publisher;

import com.cie.hr.common.domain.event.DomainEvent;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
public interface DomainEventPublisher<T extends DomainEvent> {
    void publish(T domainEvent) throws MessagingException, UnsupportedEncodingException;
}
