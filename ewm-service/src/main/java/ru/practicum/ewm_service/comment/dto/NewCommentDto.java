package ru.practicum.ewm_service.comment.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class NewCommentDto {
    @NotBlank
    @Size(max = 1000)
    private String text;
}