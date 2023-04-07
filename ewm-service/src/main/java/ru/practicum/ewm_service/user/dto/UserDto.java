package ru.practicum.ewm_service.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter

@Builder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserDto {

    private Integer id;

    @Email
    @NotNull
    @NotBlank
    @Size(max = 200)
    private String email;

    @NotNull
    @Size(max = 100)
    @NotBlank
    private String name;
}
