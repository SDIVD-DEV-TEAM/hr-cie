package com.cie.hr.common.event.publish;

import com.cie.hr.common.domain.event.publisher.DomainEventPublisher;
import com.cie.hr.common.event.OnResetPasswordEvent;

/**
 * @author Alexis TAMBIE
 * @created 19/05/2023
 * @project hr-cie
 */
public interface OnResetPasswordRequestMessagePublisher extends DomainEventPublisher<OnResetPasswordEvent> {
}
