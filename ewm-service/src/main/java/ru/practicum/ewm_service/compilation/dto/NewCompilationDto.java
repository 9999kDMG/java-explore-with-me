package ru.practicum.ewm_service.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class NewCompilationDto {
    private List<Integer> events;
    private boolean pinned;

    @NotBlank
    @Size(max = 100)
    private String title;
}
