package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings_model")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;
    @Column(name = "booking_start", nullable = false)
    private LocalDateTime start;
    @Column(name = "booking_end", nullable = false)
    private LocalDateTime end;
    @ManyToOne
    @JoinColumn(name = "booking_item", nullable = false)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "booking_booker", nullable = false)
    private User booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false)
    private BookingStatus status;
}
