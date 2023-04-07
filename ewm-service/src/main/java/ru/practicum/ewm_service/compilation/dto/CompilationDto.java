package ru.practicum.ewm_service.compilation.dto;

import lombok.*;
import ru.practicum.ewm_service.event.dto.EventShortDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class CompilationDto {
    private Integer id;
    private List<EventShortDto> events;
    private Boolean pinned;
    private String title;
}
