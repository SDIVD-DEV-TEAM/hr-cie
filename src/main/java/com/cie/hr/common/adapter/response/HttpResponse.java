package com.cie.hr.common.adapter.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

/**
 * @author Alexis TAMBIE
 * @created 04/05/2023
 * @project hr-cie
 */
@Setter
@Getter
public class HttpResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "America/New_York")
    private Date timeStamp;
    private int code; // 200, 201, 400, 500
    private HttpStatus httpStatus;
    private String reason;
    private String message;

    public HttpResponse(int code, HttpStatus httpStatus, String reason, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }


}
