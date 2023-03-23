package ru.practicum.stats.server.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.StatsHitDto;
import ru.practicum.stats.dto.StatsViewDto;
import ru.practicum.stats.server.mapper.StatsMapper;
import ru.practicum.stats.server.model.Stats;
import ru.practicum.stats.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class StatsService {
    private final StatsRepository statsRepository;

    public StatsHitDto create(StatsHitDto statsHitDto) {
        Stats stats = StatsMapper.toStats(statsHitDto);
        stats = statsRepository.save(stats);
        return StatsMapper.toStatsHitDto(stats);
    }

    public List<StatsViewDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findAllUniqueStatsByUris(start, end, uris);
            }
            return statsRepository.findAllUniqueStats(start, end);
        } else {
            if (uris != null && !uris.isEmpty()) {
                return statsRepository.findAllStatsByUris(start, end, uris);
            }
            return statsRepository.findAllStats(start, end);
        }
    }
}
