package com.cie.hr.common.exception;

/**
 * @author Koty BLEU
 * @created 07/05/2023
 * @project hr
 */
public class DomainException extends RuntimeException {
    public DomainException() { super();}
    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
