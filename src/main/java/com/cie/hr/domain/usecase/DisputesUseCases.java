package com.cie.hr.domain.usecase;

import com.cie.hr.application.command.CreateDisputeCommand;
import com.cie.hr.application.command.UpdateDisputesCommand;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * @author Alexis TAMBIE
 * @created 22/06/2023
 * @project hr-cie
 */
public interface DisputesUseCases {
    UUID createDispute(CreateDisputeCommand command) throws MessagingException, UnsupportedEncodingException;
    UUID updateDispute(UpdateDisputesCommand command) throws MessagingException, UnsupportedEncodingException;
}
