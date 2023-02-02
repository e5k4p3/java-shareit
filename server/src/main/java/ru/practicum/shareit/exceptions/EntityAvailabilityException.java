package ru.practicum.shareit.exceptions;

public class EntityAvailabilityException extends RuntimeException {
    public EntityAvailabilityException(final String message) {
        super(message);
    }
}
