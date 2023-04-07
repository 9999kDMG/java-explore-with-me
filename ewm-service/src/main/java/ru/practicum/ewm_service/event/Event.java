package ru.practicum.ewm_service.event;

import lombok.*;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Integer id;

    @Column(name = "annotation", length = 2000, nullable = false)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    @Column(name = "description", length = 7000, nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Embedded
    private Location location;

    @Column(name = "paid", nullable = false)
    private Boolean paid;

    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;

    @Column(name = "PUBLISHED_ON", nullable = false)
    private LocalDateTime publishedOn;

    @Column(name = "REQUEST_MODERATION", nullable = false)
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATE", length = 100, nullable = false)
    private EventState state;

    @Column(name = "TITLE", length = 120, nullable = false)
    private String title;

    @Column(name = "CONFIRMED_REQUESTS")
    private Integer confirmedRequests;
}
