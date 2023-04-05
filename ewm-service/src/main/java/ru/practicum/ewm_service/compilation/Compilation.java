package ru.practicum.ewm_service.compilation;

import lombok.*;
import ru.practicum.ewm_service.event.Event;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder

@Entity
@Table(name = "compilations")
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compilation_id")
    private Integer id;

    @Column(name = "pinned", nullable = false)
    private Boolean pinned;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "event_compilations",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id"))
    Set<Event> events;
}
