package com.cie.hr.common.event.publish;

import com.cie.hr.common.domain.event.publisher.DomainEventPublisher;
import com.cie.hr.common.event.CreateEmployeeEvent;

/**
 * @author Alexis TAMBIE
 * @created 08/05/2023
 * @project hr-cie
 */
public interface CreateEmployeeRequestMessagePublisher extends DomainEventPublisher<CreateEmployeeEvent> {

}
