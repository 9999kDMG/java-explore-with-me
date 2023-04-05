package ru.practicum.ewm_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm_service.event.Location;
import ru.practicum.ewm_service.event.StateActionAdmin;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class UpdateEventsAdminRequest {
    @Size(max = 2000, min = 20)
    private String annotation;

    private Integer category;

    @Size(max = 7000, min = 20)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionAdmin stateAction;

    @Size(max = 120, min = 3)
    private String title;
}
