package ru.practicum.stats.dto;

import lombok.*;

@Setter
@Getter

@Builder

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StatsViewDto {
    private String app;
    private String uri;
    private Long hits;
}
