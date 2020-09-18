package com.antonio.authserver.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private int status;
    private String message;
    private Object result;

    public ApiResponse(int status, String message, Object result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    public ApiResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
