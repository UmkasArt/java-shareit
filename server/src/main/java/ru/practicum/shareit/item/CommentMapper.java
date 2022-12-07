package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getItem().getId(),
                comment.getAuthor().getName()
        );
    }

    public static Comment toModel(CommentDto commentDto) {
        return new Comment(
                commentDto.getId(),
                commentDto.getText()
        );
    }
}
