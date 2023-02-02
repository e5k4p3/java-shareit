package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "items_model")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(name = "item_description", nullable = false, length = 5000)
    private String description;
    @Column(name = "item_availability", nullable = false)
    private Boolean available;
    @Column(name = "item_owner_id", nullable = false)
    private Long owner;
    @Column(name = "item_request_id")
    private Long requestId;
}
