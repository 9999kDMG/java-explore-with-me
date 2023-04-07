package ru.practicum.ewm_service.user.dto;

import lombok.*;

@Setter
@Getter

@Builder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserShortDto {
    private Integer id;
    private String name;
}
