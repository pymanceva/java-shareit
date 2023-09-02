package ru.practicum.shareit.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotSavedException extends RuntimeException {
    public NotSavedException(String message) {
        super(message);
    }
}