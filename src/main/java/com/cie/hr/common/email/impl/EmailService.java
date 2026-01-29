package com.cie.hr.common.email.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static com.cie.hr.common.email.constant.EmailConstant.PNG_MIME;
import static com.cie.hr.common.email.constant.EmailConstant.SPRING_LOGO_IMAGE;
import com.cie.hr.common.email.port.EmailServicePort;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * @author Alexis TAMBIE
 * @created 07/05/2023
 * @project hr-cie
 */
@Service
public class EmailService implements EmailServicePort {
    private final JavaMailSender mailSender;
    private final Environment environment;
    private final TemplateEngine htmlTemplateEngine;

    public EmailService(JavaMailSender mailSender, Environment environment, TemplateEngine htmlTemplateEngine) {
        this.mailSender = mailSender;
        this.environment = environment;
        this.htmlTemplateEngine = htmlTemplateEngine;
    }

    @Override
    public void sendEmail(String to, String subject, String token, String templateName, String name) throws MessagingException, UnsupportedEncodingException {
        sendEmailHelper(to, subject, templateName, name, Map.of("token", token));
    }

    @Override
    public void sendCreateAccountEmail(String to, String subject, String templateName, String password, String name) throws MessagingException, UnsupportedEncodingException {
        // sendEmailHelper(to, subject, templateName, name, Map.of("password", password, "url", environment.getProperty("app.url.base", "https://evaluation-manager-cie.dctd-cie.com/")));
        sendEmailHelper(to, subject, templateName, name, Map.of("password", password, "url", environment.getProperty("app.url.base", "https://evaluation-manager.dctd-cie.com/")));
    }

    private void sendEmailHelper(String to, String subject, String templateName, String name, Map<String, Object> additionalVariables) throws MessagingException, UnsupportedEncodingException {
        String mailFrom = environment.getProperty("spring.mail.username");
        String mailFromName = environment.getProperty("mail.from.name", "Identity");

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(new InternetAddress(mailFrom, mailFromName));

        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("email", to);
        ctx.setVariable("logoCIE", SPRING_LOGO_IMAGE);
        ctx.setVariable("name", name);
        additionalVariables.forEach(ctx::setVariable);

        final String htmlContent = htmlTemplateEngine.process(templateName, ctx);
        helper.setText(htmlContent, true);
        helper.addInline("logoCIE", new ClassPathResource(SPRING_LOGO_IMAGE), PNG_MIME);

        mailSender.send(message);
    }
}
