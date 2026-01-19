package com.cie.hr.common.email.port;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

/**
 * @author Alexis TAMBIE
 * @created 07/05/2023
 * @project hr-cie
 */
public interface EmailServicePort {
    void sendEmail(String to, String subject, String token, String template_name, String name) throws MessagingException, UnsupportedEncodingException;
    void sendCreateAccountEmail(String to, String subject, String template_name, String password, String name) throws MessagingException, UnsupportedEncodingException;
}
