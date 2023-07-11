package ru.practicum.ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                                                       // 400
    public ApiError handleValidationError(final MethodArgumentNotValidException e) {
        log.info("400 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Invalid input data.")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                                                       // 400
    public ApiError handleWrongParameterError(final WrongParameterException e) {
        log.info("400 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)                                                                       // 400
    public ApiError handleWrongEventDateError(final WrongEventDateException e) {
        log.info("400 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Field: eventDate. Error: дата начала события не соответствует условию")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)                                                                         // 404
    public ApiError handleNotFoundException(final NotFoundException e) {
        log.info("404 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                                          // 409
    public ApiError handleIllegalStatusError(final IllegalEventStatusException e) {
        log.error("409 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Only pending or canceled events can be changed")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                                          // 409
    public ApiError handleWrongRequestParameterError(final WrongRequestParameterException e) {
        log.error("409 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Only pending or canceled events can be changed")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                                          // 409
    public ApiError handleDataIntegrityViolationError(final DataIntegrityViolationException e) {
        log.error("409 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)                                                                          // 409
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("409 {}", e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .reason("Integrity constraint has been violated.")
                .message(e.getMessage())
                .errors(List.of(e.getClass().getName()))
                .timestamp(LocalDateTime.now()).build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)                                                             // 500
    public ApiError handleException(final Exception e) {
        log.error("Error", e);
        StringWriter out = new StringWriter();
        e.printStackTrace(new PrintWriter(out));
        String stackTrace = out.toString();
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("НЕПРЕДВИДЕННАЯ ОШИБКА")
                .message(e.getMessage())
                .errors(Collections.singletonList(stackTrace))
                .timestamp(LocalDateTime.now()).build();
    }

}
