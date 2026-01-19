package com.cie.hr.application.command;

import jakarta.mail.MessagingException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;

import java.io.IOException;
import java.util.Set;

/**
 * @author Koty BLEU
 * @created 03/05/2023
 * @project hr
 */
@SuppressWarnings("rawtypes")
public interface Command<T, R> {
    default void checkValidity() {
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        var validator = validatorFactory.getValidator();
        Set<ConstraintViolation<Command>> validate = validator.validate(this);
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException(validate);
        }
    }

    R execute(T useCase) throws MessagingException, IOException;
}

