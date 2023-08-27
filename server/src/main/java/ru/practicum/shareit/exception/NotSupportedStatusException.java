package ru.practicum.shareit.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotSupportedStatusException extends RuntimeException {
    public NotSupportedStatusException(String message) {
        super(message);
    }
}
