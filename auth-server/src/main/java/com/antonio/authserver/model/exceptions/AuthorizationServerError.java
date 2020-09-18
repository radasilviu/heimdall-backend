package com.antonio.authserver.model.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class AuthorizationServerError {

    private HttpStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime timestamp;
    private String message;


    public AuthorizationServerError(HttpStatus status, LocalDateTime timestamp, String message) {
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
    }


}
