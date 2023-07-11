package ru.practicum.ewm.exception;

public class WrongEventParameterException extends RuntimeException {
    public WrongEventParameterException(String message) {
        super(message);
    }
}
