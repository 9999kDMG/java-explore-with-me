package ru.practicum.ewm_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm_service.event.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class NewEventDto {
    @NotBlank
    @NotNull
    @Size(max = 2000, min = 20)
    private String annotation;

    @NotNull
    private Integer category;

    @NotBlank
    @NotNull
    @Size(max = 7000, min = 20)
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;

    @NotNull
    @NotBlank
    @Size(max = 120, min = 3)
    private String title;
}
