package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_requests_model")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;
    @Column(name = "request_description", nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "request_requester_id", nullable = false)
    private User requester;
    @Column(name = "request_created", nullable = false)
    private LocalDateTime created;
}
