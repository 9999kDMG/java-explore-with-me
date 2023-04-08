package ru.practicum.ewm_service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm_service.comment.CommentState;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class CommentDto {
    private Integer id;
    private String text;
    private UserShortDto user;
    private Integer eventId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private CommentState state;
}