package ru.practicum.ewm_service.event;

import ru.practicum.ewm_service.category.CategoryMapper;
import ru.practicum.ewm_service.event.dto.EventFullDto;
import ru.practicum.ewm_service.event.dto.EventShortDto;
import ru.practicum.ewm_service.event.dto.NewEventDto;
import ru.practicum.ewm_service.user.UserMapper;

public class EventMapper {
    private static final Boolean DEFAULT_PAID = false;
    private static final Integer DEFAULT_PARTICIPANT_LIMIT = 0;
    private static final Boolean DEFAULT_REQUEST_MODERATION = true;

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(newEventDto.getEventDate())
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid() != null ? newEventDto.getPaid() : DEFAULT_PAID)
                .participantLimit(newEventDto.getParticipantLimit() != null
                        ? newEventDto.getParticipantLimit() : DEFAULT_PARTICIPANT_LIMIT)
                .requestModeration(newEventDto.getRequestModeration() != null
                        ? newEventDto.getRequestModeration() : DEFAULT_REQUEST_MODERATION)
                .title(newEventDto.getTitle())
                .build();
    }

    public static EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public static EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }
}
