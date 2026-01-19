package com.cie.hr.common.event.publish;

import com.cie.hr.common.domain.event.DomainEvent;
import com.cie.hr.common.event.StartCampaignEvent;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * @author Alexis TAMBIE
 * @created 24/06/2023
 * @project hr-cie
 */
public interface StartCampaignRequestMessagePublisher extends DomainEvent<StartCampaignEvent> {

    void publishListWithParam(Date end_date, List<StartCampaignEvent> startCampaignEventList) throws MessagingException, UnsupportedEncodingException;

}
