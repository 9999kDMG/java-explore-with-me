package ru.practicum.stats.server.mapper;

import ru.practicum.stats.dto.StatsHitDto;
import ru.practicum.stats.server.model.Stats;

public class StatsMapper {
    public static Stats toStats(StatsHitDto statsHitDto) {
        return Stats.builder()
                .app(statsHitDto.getApp())
                .uri(statsHitDto.getUri())
                .ip(statsHitDto.getIp())
                .timestamp(statsHitDto.getTimestamp())
                .build();
    }

    public static StatsHitDto toStatsHitDto(Stats stats) {
        return StatsHitDto.builder()
                .id(stats.getId())
                .app(stats.getApp())
                .uri(stats.getUri())
                .ip(stats.getIp())
                .timestamp(stats.getTimestamp())
                .build();
    }
}
