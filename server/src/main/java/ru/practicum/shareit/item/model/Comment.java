package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    @ManyToOne
    @JoinColumn(
            name = "ITEM_ID",
            referencedColumnName = "ID"
    )
    private Item item;
    @OneToOne
    @JoinColumn(
            name = "AUTHOR_ID",
            referencedColumnName = "id"
    )
    private User author;


    public Comment(Long id, String text) {
        this.id = id;
        this.text = text;
    }
}