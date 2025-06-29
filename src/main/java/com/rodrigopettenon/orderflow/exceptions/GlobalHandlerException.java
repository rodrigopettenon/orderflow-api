package com.rodrigopettenon.orderflow.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ClientErrorException.class)
    public ResponseEntity<Object> clientExceptionHandler(ClientErrorException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", exception.getMessage());
        body.put("object", exception.getObject());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

}
