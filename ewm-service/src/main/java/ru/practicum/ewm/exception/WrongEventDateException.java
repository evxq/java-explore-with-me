package ru.practicum.ewm.exception;

public class WrongEventDateException extends RuntimeException {
    public WrongEventDateException(String message) {
        super(message);
    }
}
