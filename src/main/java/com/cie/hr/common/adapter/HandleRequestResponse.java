package com.cie.hr.common.adapter;

import com.cie.hr.common.exception.ApplicationException;
import com.cie.hr.common.exception.DomainException;
import com.cie.hr.common.exception.InfrastructureException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

import static com.cie.hr.common.constant.Constant.HTTP_MESSAGE_OK;

/**
 * @author Alexis TAMBIE
 * @created 30/09/2024
 * @project hr-cie
 */
@Component
public class HandleRequestResponse {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public ResponseEntity<BaseResponseEntity<Object>> handleRequest(Supplier<Object> request) {
        try {
            Object data = request.get();
            return createResponseEntity(HttpStatus.OK.value(), HTTP_MESSAGE_OK, false, data, null, HttpStatus.OK);
        } catch (ApplicationException | InfrastructureException | DomainException ex) {
            LOGGER.error("ApplicationException | InfrastructureException | DomainException : {}", ex.getMessage());
            return createResponseEntity(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), true, null, ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException ex) {
            LOGGER.error("ConstraintViolationException : {}", ex.getMessage());
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception ex) {
            LOGGER.error("Exception : {}", ex.getMessage());
            return createResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while processing the request", true, null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<BaseResponseEntity<Object>> createResponseEntity(int code, String message, boolean status, Object data, Object errors, HttpStatus httpStatus) {
        return new ResponseEntity<>(new BaseResponseEntity<>(code, message, status, data, errors), httpStatus);
    }
}
