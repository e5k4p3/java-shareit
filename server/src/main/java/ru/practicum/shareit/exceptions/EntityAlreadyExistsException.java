package ru.practicum.shareit.exceptions;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(final String message) {
        super(message);
    }
}
