package com.cie.hr.common.exception;

/**
 * @author Koty BLEU
 * @created 07/05/2023
 * @project hr
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException() { super();}
    public ApplicationException(String message) { super(message);}
}
