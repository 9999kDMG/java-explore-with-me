package ru.practicum.ewm_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm_service.category.dto.CategoryDto;
import ru.practicum.ewm_service.user.dto.UserShortDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class EventShortDto {
    private Integer id;
    private CategoryDto category;
    private String annotation;
    private Integer confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}