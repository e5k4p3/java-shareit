package ru.practicum.shareit.exceptions.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class,
        ItemController.class,
        BookingController.class,
        ItemRequestController.class})
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(final ValidationException e) {
        log.error(e.getMessage());
        return new ErrorResponse("Ошибка валидации.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage(), "Недопустимый аргумент.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingHeader(final MissingRequestHeaderException e) {
        log.error(e.getMessage());
        return new ErrorResponse("У запроса отсутсвует заголовок.", e.getMessage());
    }
}
