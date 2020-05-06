package com.dialogs.controller;

import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dialogs.service.CompanyNotFoundException;
import com.dialogs.service.CompanyServiceException;
import com.dialogs.service.DialogNotFoundException;

/**
 * General exceptions handler for rest controllers
 */
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CompanyServiceException.class)
    protected ResponseEntity<Object> handleServiceException(CompanyServiceException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CompanyNotFoundException.class)
    protected ResponseEntity<Object> companyNotFoundException(CompanyNotFoundException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DialogNotFoundException.class)
    protected ResponseEntity<Object> dialogNotFoundException(DialogNotFoundException ex) {
        Map<String, Object> body = createErrorBody(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> createErrorBody(HttpStatus status, Object error) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalTime.now());
        body.put("status", status.value());
        body.put("error", error);
        return body;
    }
}
