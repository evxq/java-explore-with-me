package ru.practicum.ewm.exception;

public class WrongRequestParameterException extends RuntimeException {
    public WrongRequestParameterException(String message) {
        super(message);
    }
}
