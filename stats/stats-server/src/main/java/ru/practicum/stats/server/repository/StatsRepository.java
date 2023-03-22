package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.dto.StatsViewDto;
import ru.practicum.stats.server.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Integer> {

    @Query("SELECT new ru.practicum.stats.dto.StatsViewDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "  AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.id) DESC")
    List<StatsViewDto> findAllUniqueStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query("SELECT new ru.practicum.stats.dto.StatsViewDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.id) DESC")
    List<StatsViewDto> findAllUniqueStats(LocalDateTime start, LocalDateTime end);


    @Query("SELECT new ru.practicum.stats.dto.StatsViewDto(s.app, s.uri, COUNT(s.id)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "  AND s.uri IN :uris " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.id) DESC")
    List<StatsViewDto> findAllStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);


    @Query("SELECT new ru.practicum.stats.dto.StatsViewDto(s.app, s.uri, COUNT(s.id)) " +
            "FROM Stats s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.id) DESC")
    List<StatsViewDto> findAllStats(LocalDateTime start, LocalDateTime end);
}
