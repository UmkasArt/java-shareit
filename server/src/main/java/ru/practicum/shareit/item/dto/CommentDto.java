package ru.practicum.shareit.item.dto;

import lombok.*;
@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
}