package ru.practicum.ewm_service.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter

@Builder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CategoryDto {

    private Integer id;
    @NotBlank
    @NotNull
    @Size(max = 100)
    private String name;
}
