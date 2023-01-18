package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.enums.BookingStatus;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long id;
    @NotNull(message = "Время старта бронирования не может быть пустым.")
    @FutureOrPresent(message = "Время старта бронирования должно быть в будущем или настоящем.")
    private LocalDateTime start;
    @NotNull(message = "Время окончания бронирования не может быть пустым.")
    @Future(message = "Время окончания бронирования должно быть в будущем.")
    private LocalDateTime end;
    @NotNull(message = "Id бронируемого предмета не может быть пустым.")
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}
