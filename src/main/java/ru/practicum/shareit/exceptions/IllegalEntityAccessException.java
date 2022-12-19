package ru.practicum.shareit.exceptions;

public class IllegalEntityAccessException extends RuntimeException {
    public IllegalEntityAccessException(final String message) {
        super(message);
    }
}
