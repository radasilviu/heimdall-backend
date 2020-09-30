package com.antonio.authserver.model.exceptions;

import com.antonio.authserver.model.CustomException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = { CustomException.class })
    protected ResponseEntity<Object> handleNotFoundObject(
            CustomException ex, WebRequest request) {

        Map<String, String> body = new HashMap<>();
        body.put("message", ex.getMessage());
        return handleExceptionInternal(ex, body, new HttpHeaders(), ex.getHttpStatus(), request);
    }
}
