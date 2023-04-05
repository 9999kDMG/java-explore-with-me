package ru.practicum.ewm_service.compilation.dto;

import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class UpdateCompilationRequest {
    private List<Integer> events;
    private Boolean pinned;

    @Size(max = 120)
    private String title;
}
