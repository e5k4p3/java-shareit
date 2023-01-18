package ru.practicum.shareit.exceptions;

public class UnsupportedStateException extends RuntimeException {
    public UnsupportedStateException(final String message) {
        super(message);
    }
}
