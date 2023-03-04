package ru.practicum.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    private ResponseEntity<Object> handlePSQLException(DataIntegrityViolationException ex) {
        log.info("409 {}", ex.getMessage());
        return new ResponseEntity<>(new ExceptionDto(
                ex.getMessage(),
                ex.getCause().getMessage(),
                HttpStatus.CONFLICT.name(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }

}
