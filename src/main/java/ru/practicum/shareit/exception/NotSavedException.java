package ru.practicum.shareit.exception;

public class NotSavedException extends RuntimeException {
    public NotSavedException(String message) {
        super(message);
    }
}