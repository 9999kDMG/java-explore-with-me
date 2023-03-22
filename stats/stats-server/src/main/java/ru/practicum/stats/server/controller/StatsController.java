package ru.practicum.stats.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.StatsHitDto;
import ru.practicum.stats.dto.StatsViewDto;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@RestController

@RequiredArgsConstructor

public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public StatsHitDto postStats(@RequestBody StatsHitDto statsHitDto) {
        return statsService.create(statsHitDto);
    }

    @GetMapping("/stats")
    public List<StatsViewDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
