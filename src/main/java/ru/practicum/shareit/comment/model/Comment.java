package ru.practicum.shareit.comment.model;

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
@Table(name = "comments_model")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @Column(name = "comment_text", length = 5000)
    private String text;
    @ManyToOne
    @JoinColumn(name = "comment_user_id")
    private User author;
    @Column(name = "comment_item_id")
    private Long itemId;
    @Column(name = "comment_created")
    private LocalDateTime created;
}
