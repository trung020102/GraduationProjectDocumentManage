package com.doc.mamagement.web.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GeneralExceptionHandler {
    @ExceptionHandler(UnprocessableException.class)
    public ResponseEntity<?> invalidFileExceptionHandling() {
        return new ResponseEntity<>(new ErrorDetails("message.error.file", HttpStatus.UNPROCESSABLE_ENTITY.value(), null), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<?> noPermissionExceptionHandling() {
        return new ResponseEntity<>(new ErrorDetails("message.error.file", HttpStatus.FORBIDDEN.value(), null), HttpStatus.FORBIDDEN);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ErrorDetails {
        private String message;
        private int errorCode;
        private Map<String, String> errors;
    }

    @ExceptionHandler ({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errorDetails = new HashMap<>();

        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            // Handle to get main property
            String[] parts = propertyPath.split("\\.");
            String mainProperty = parts[parts.length - 1];
            String errorMessage = violation.getMessage();
            errorDetails.put(mainProperty, errorMessage);
        }
        return ResponseEntity.badRequest().body(new ErrorDetails(ex.getMessage(), HttpStatus.BAD_REQUEST.value(), errorDetails));
    }
}
