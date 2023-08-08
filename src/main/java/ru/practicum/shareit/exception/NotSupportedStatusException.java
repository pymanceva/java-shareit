package ru.practicum.shareit.exception;

public class NotSupportedStatusException extends RuntimeException {
    public NotSupportedStatusException(String message) {
        super(message);
    }
}
