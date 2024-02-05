package com.lspeixotodev.springboottesting.exception;

import com.lspeixotodev.springboottesting.exception.entity.ErrorDetails;

import org.springframework.http.*;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ErrorDetails> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest
    ) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        ErrorDetails errorDetails = new ErrorDetails(
                Instant.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .contentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
                .body(errorDetails);
    }

    @ExceptionHandler({ResourceAlreadyExistsException.class})
    public ResponseEntity<ErrorDetails> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException exception,
            WebRequest webRequest
    ) {

        HttpStatus status = HttpStatus.CONFLICT;

        ErrorDetails errorDetails = new ErrorDetails(
                Instant.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .contentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
                .body(errorDetails);
    }

    @ExceptionHandler({PlatformException.class})
    public ResponseEntity<ErrorDetails> handlePlatformExceptionException(
            PlatformException exception,
            WebRequest webRequest
    ) {

        HttpStatus status = exception.getStatus();

        ErrorDetails errorDetails = new ErrorDetails(
                Instant.now(),
                exception.getMessage(),
                webRequest.getDescription(false),
                status.value()
        );

        return ResponseEntity
                .status(status)
                .contentType(org.springframework.http.MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)))
                .body(errorDetails);
    }

}
