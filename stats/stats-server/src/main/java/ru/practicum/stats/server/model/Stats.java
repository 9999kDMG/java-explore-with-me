package ru.practicum.stats.server.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder

@Entity
@Table(name = "stats")
public class Stats {
    @Column(name = "stats_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app", nullable = false, length = 30)
    private String app;

    @Column(name = "uri", nullable = false, length = 2000)
    private String uri;

    @Column(name = "ip", nullable = false, length = 15)
    private String ip;

    @Column(name = "request_time")
    private LocalDateTime timestamp;
}
