package com.cie.hr.common.adapter;

import lombok.Getter;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
@Getter
public class BaseResponseEntity<T> {
    int code;

    String message;

    boolean status;

    T data;

    Object errors;

    public BaseResponseEntity(int code, String message, boolean status, T data, Object errors) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.data = data;
        this.errors = errors;
    }
}
