package ru.practicum.ewm_service.request;

import lombok.*;
import ru.practicum.ewm_service.event.Event;
import ru.practicum.ewm_service.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder

@Entity
@Table(name = "REQUESTS")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;

    @Column(name = "STATUS")
    private RequestStatus status;

    @Column(name = "CREATED")
    private LocalDateTime created;
}
