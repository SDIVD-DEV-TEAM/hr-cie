package com.cie.hr.common.exception.handler;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.DomainException;
import com.cie.hr.common.exception.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author Koty BLEU
 * @created 07/05/2023
 * @project hr
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    String INTERNAL_ERR_MSG = "Une erreur est survenue lors du traitement de votre requête";
    String INTERNAL_DOMAIN_ERR_MSG = "Une erreur est survenue lors du traitement de votre requête -- domain";
    String INTERNAL_APP_ERR_MSG = "Une erreur est survenue lors du traitement de votre requête -- app";
    String INTERNAL_INFRA_ERR_MSG = "Une erreur est survenue lors du traitement de votre requête -- infra";

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(value = {DomainException.class})
    protected ResponseEntity<Object> handlerAllDomainsErrors(DomainException ex, WebRequest request) {
        //ex.printStackTrace();
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(INTERNAL_DOMAIN_ERR_MSG, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {ApplicationException.class})
    protected ResponseEntity<Object> handlerAllApplicationsErrors(ApplicationException ex, WebRequest request) {
        //ex.printStackTrace();
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(INTERNAL_APP_ERR_MSG, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InfrastructureException.class)
    protected ResponseEntity<Object> handlerAllInfraErrors(DomainException ex, WebRequest request) {
        //ex.printStackTrace();
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(INTERNAL_INFRA_ERR_MSG, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handlerAllErrors(Exception ex, WebRequest request) {
        //ex.printStackTrace();
        LOGGER.error(ex.getMessage());
        return new ResponseEntity<>(INTERNAL_ERR_MSG, new HttpHeaders(), INTERNAL_SERVER_ERROR);
    }
}
