package com.cie.hr.common.exception;

/**
 * @author Koty BLEU
 * @created 07/05/2023
 * @project hr
 */
public class InfrastructureException extends RuntimeException {
    public InfrastructureException() {
        super();
    }
    public InfrastructureException(String message) {
        super(message);
    }
}
