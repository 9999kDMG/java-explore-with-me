package ru.practicum.ewm_service.comment;

import ru.practicum.ewm_service.comment.dto.CommentDto;
import ru.practicum.ewm_service.comment.dto.CommentShortDto;
import ru.practicum.ewm_service.user.UserMapper;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .eventId(comment.getEvent().getId())
                .created(comment.getCreated())
                .text(comment.getText())
                .state(comment.getState())
                .build();
    }

    public static CommentShortDto toCommentShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .user(UserMapper.toUserShortDto(comment.getUser()))
                .created(comment.getCreated())
                .build();
    }
}
