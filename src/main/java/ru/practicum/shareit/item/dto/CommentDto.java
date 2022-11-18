package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private Long itemId;
    private String authorName;
}
