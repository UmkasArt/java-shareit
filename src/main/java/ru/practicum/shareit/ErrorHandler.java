package ru.practicum.shareit;

import org.apache.catalina.connector.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<?> handleIllegalArgumentException(final IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorWrapper(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleValidationException(final ValidationException e) {
        return new ResponseEntity<>(new ErrorWrapper(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> handleValidationException(final NoSuchElementException e) {
        return new ResponseEntity<>(new ErrorWrapper(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
