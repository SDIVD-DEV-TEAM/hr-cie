package com.cie.hr.common.security.utility;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author Alexis TAMBIE
 * @created 10/05/2023
 * @project hr-cie
 */
@Getter
public class ExceptionResponse {
    private String message;
    private LocalDateTime dateTime;


    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
