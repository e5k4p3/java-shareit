package ru.practicum.shareit.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.*;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.user.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        BookingController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExists(final EntityAlreadyExistsException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Такой объект уже существует.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final EntityNotFoundException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Объект не найден.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleIllegalAccess(final IllegalEntityAccessException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Отсутствие прав доступа к объекту.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(final MissingRequestHeaderException e) {
        log.error(e.getMessage());
        return new ErrorResponse("У запроса отсутсвует заголовок.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAvailability(final EntityAvailabilityException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Недоступность объекта.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Недопустимый аргумент.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUnsupportedState(final UnsupportedStateException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), "Необрабатываемый аргумент."); // из за постмана, пришлось поменять местами...
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleForbidden(final ForbiddenAccessException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Отсутствие прав доступа.", e.getMessage());
    }
//    из за тестов постмана, пришлось наплодить столько новых обработчиков...
}
