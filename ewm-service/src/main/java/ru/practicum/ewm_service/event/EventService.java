package ru.practicum.ewm_service.event;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.category.Category;
import ru.practicum.ewm_service.category.CategoryService;
import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.exception.BadRequestException;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.request.*;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserService;
import ru.practicum.ewm_service.utils.Pagination;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final StatsClient statsClient;
    private final RequestRepository requestRepository;

    private final int TIME_LIMIT_OF_REQUEST = 2;
    private final int TIME_LIMIT_OF_UPDATE = 1;

    //methods for private part of controller
    public EventFullDto postEventByUser(Integer userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(TIME_LIMIT_OF_REQUEST))) {
            throw new ConflictException("incorrect event time");
        }

        User user = userService.getUserOrThrow(userId);
        Category category = categoryService.getCategoryOrThrow(newEventDto.getCategory());

        Event event = EventMapper.toEvent(newEventDto);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setInitiator(user);
        event.setCategory(category);
        event.setConfirmedRequests(0);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));
        eventFullDto.setViews(0L);

        return eventFullDto;
    }

    public EventFullDto getEventOfUserById(Integer userId, Integer eventId) {
        User user = userService.getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (!user.equals(event.getInitiator())) {
            throw new NotFoundException("the user does not have this event");
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(view);

        return eventFullDto;
    }

    public List<EventFullDto> getEventsByUser(Integer userId, Integer from, Integer size) {
        userService.getUserOrThrow(userId);

        Pageable pageable = Pagination.getPageOrThrow(from, size);

        List<Event> events = eventRepository.findAllByInitiatorIdOrderByCreatedOnDesc(userId, pageable);

        return createEventFullDtoWithView(events);
    }

    public EventFullDto patchEventByUser(Integer userId,
                                         Integer eventId,
                                         UpdateEventsUserRequest updateEventsUserRequest) {

        User user = userService.getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (updateEventsUserRequest.getEventDate() != null
                && updateEventsUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(TIME_LIMIT_OF_REQUEST))) {
            throw new ConflictException("incorrect event time");
        }

        if (!user.equals(event.getInitiator())) {
            throw new NotFoundException(String.format("the event id N%s has a different owner", eventId));
        }
        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("you cannot change a confirmed event");
        }
        if (updateEventsUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventsUserRequest.getAnnotation());
        }
        if (updateEventsUserRequest.getCategory() != null) {
            Category category = categoryService.getCategoryOrThrow(updateEventsUserRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventsUserRequest.getDescription() != null) {
            event.setDescription(updateEventsUserRequest.getDescription());
        }
        if (updateEventsUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventsUserRequest.getEventDate());
        }
        if (updateEventsUserRequest.getLocation() != null) {
            event.setLocation(updateEventsUserRequest.getLocation());
        }
        if (updateEventsUserRequest.getPaid() != null) {
            event.setPaid(updateEventsUserRequest.getPaid());
        }
        if (updateEventsUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventsUserRequest.getParticipantLimit());
        }
        if (updateEventsUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventsUserRequest.getRequestModeration());
        }
        StateActionUser stateActionUser = updateEventsUserRequest.getStateAction();
        if (stateActionUser == null) {
            event.setState(EventState.PENDING);
        }
        if (stateActionUser != null) {
            if (StateActionUser.CANCEL_REVIEW.equals(stateActionUser)) {
                event.setState(EventState.CANCELED);
            } else if (StateActionUser.SEND_TO_REVIEW.equals(stateActionUser)) {
                event.setState(EventState.PENDING);
            }
        }
        if (updateEventsUserRequest.getTitle() != null) {
            event.setTitle(updateEventsUserRequest.getTitle());
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));

        Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(view);

        return eventFullDto;
    }

    public List<RequestDto> getRequestsByUserEvent(Integer userId, Integer eventId) {
        userService.getUserOrThrow(userId);
        getEventOrThrow(eventId);
        return requestRepository.findByEventInitiatorIdAndEventId(userId, eventId)
                .stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    public EventsRequestStatusUpdateResult updateRequestsByUserEvent(Integer userId,
                                                                     Integer eventId,
                                                                     EventsRequestStatusUpdateRequest updateRequest) {
        userService.getUserOrThrow(userId);
        Event event = getEventOrThrow(eventId);

        if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0 && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("exceeded the limit of requests per event id N%s", eventId));
        }

        List<Request> requests = requestRepository
                .findAllByParam(userId, eventId, updateRequest.getRequestIds());
        if (requests.stream().anyMatch(request -> !RequestStatus.PENDING.equals(request.getStatus()))) {
            throw new ConflictException("the request status should be PENDING");
        }

        List<Request> rejectedRequest = new ArrayList<>();
        List<Request> confirmedRequest = new ArrayList<>();
        EventsRequestStatusUpdateResult result = new EventsRequestStatusUpdateResult(null, null);
        switch (updateRequest.getStatus()) {
            case REJECTED:
                requests.forEach(request -> {
                    request.setStatus(RequestStatus.REJECTED);
                    rejectedRequest.add(request);
                });
                requestRepository.saveAll(rejectedRequest);
                result.setConfirmedRequests(List.of());
                result.setRejectedRequests(rejectedRequest.isEmpty() ? List.of() : rejectedRequest
                        .stream().
                        map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));
                return result;
            case CONFIRMED:
                requests.forEach(request -> {
                    if (event.getParticipantLimit() - event.getConfirmedRequests() > 0
                            || event.getParticipantLimit() == 0) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        confirmedRequest.add(request);
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        rejectedRequest.add(request);
                    }
                });

                requestRepository.saveAll(rejectedRequest);
                requestRepository.saveAll(confirmedRequest);

                event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequest.size());
                eventRepository.save(event);

                result.setConfirmedRequests(confirmedRequest.isEmpty() ? List.of() : confirmedRequest
                        .stream().
                        map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));
                result.setRejectedRequests(rejectedRequest.isEmpty() ? List.of() : rejectedRequest
                        .stream().
                        map(RequestMapper::toRequestDto)
                        .collect(Collectors.toList()));
        }
        return result;
    }

    //methods for admin part of controller
    public List<EventFullDto> getAllEventsWithParam(List<Integer> users,
                                                    List<EventState> states,
                                                    List<Integer> categories,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    Integer from, Integer size) {
        Pageable pageable = Pagination.getPageOrThrow(from, size);
        QEvent qEvent = QEvent.event;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if (users != null && !users.isEmpty()) {
            booleanBuilder.and(qEvent.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            booleanBuilder.and(qEvent.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            booleanBuilder.and(qEvent.category.id.in(categories));
        }

        if (rangeStart != null) {
            booleanBuilder.and(qEvent.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            booleanBuilder.and(qEvent.eventDate.loe(rangeEnd));
        }

        List<Event> events = eventRepository.findAll(booleanBuilder, pageable).getContent();

        return createEventFullDtoWithView(events);
    }

    public EventFullDto patchEventByAdmin(Integer eventId, UpdateEventsAdminRequest updateRequest) {
        if (updateRequest.getEventDate() != null &&
                updateRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(TIME_LIMIT_OF_UPDATE))) {
            throw new ConflictException("Incorrect event time");
        }
        Event event = getEventOrThrow(eventId);

        if (updateRequest.getStateAction() != null) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("status of event is not PENDING");
            }

            if (updateRequest.getStateAction() == StateActionAdmin.PUBLISH_EVENT) {
                event.setState(EventState.PUBLISHED);
            } else if (updateRequest.getStateAction() == StateActionAdmin.REJECT_EVENT) {
                event.setState(EventState.CANCELED);
            }
        }

        if (updateRequest.getAnnotation() != null) {
            event.setAnnotation(updateRequest.getAnnotation());
        }

        if (updateRequest.getCategory() != null) {
            Category category = categoryService.getCategoryOrThrow(updateRequest.getCategory());
            event.setCategory(category);
        }

        if ((updateRequest.getDescription() != null)) {
            event.setDescription(updateRequest.getDescription());
        }

        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }

        if (updateRequest.getLocation() != null) {
            event.setLocation(updateRequest.getLocation());
        }

        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }

        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }

        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }

        if (updateRequest.getTitle() != null) {
            event.setTitle(updateRequest.getTitle());
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(eventRepository.save(event));

        Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(view);

        return eventFullDto;
    }

    //methods for public part of controller
    public List<EventShortDto> getEventsByPublicUser(String text,
                                                     List<Integer> categories,
                                                     Boolean paid,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable,
                                                     EventSort sort, Integer from, Integer size) {
        if (size <= 0 || from < 0) {
            throw new BadRequestException("incorrect page parameters");
        }

        QEvent qEvent = QEvent.event;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qEvent.state.eq(EventState.PUBLISHED));
        if (text != null && !text.isBlank()) {
            booleanBuilder.and(qEvent.annotation.containsIgnoreCase(text)
                    .or(qEvent.description.containsIgnoreCase(text)));
        }

        if (categories != null && !categories.isEmpty()) {
            booleanBuilder.and(qEvent.category.id.in(categories));
        }

        if (paid != null) {
            booleanBuilder.and(qEvent.paid.eq(paid));
        }

        if (rangeStart == null && rangeEnd == null) {
            booleanBuilder.and(qEvent.eventDate.after(LocalDateTime.now()));
        }

        if (rangeStart != null) {
            booleanBuilder.and(qEvent.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            booleanBuilder.and(qEvent.eventDate.loe(rangeEnd));
        }

        if (onlyAvailable) {
            booleanBuilder.and(qEvent.confirmedRequests.lt(qEvent.participantLimit)
                    .or(qEvent.participantLimit.eq(0))); //check this
        }

        Pageable pageable;

        if (EventSort.EVENT_DATE.equals(sort)) {
            pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "eventDate"));
        } else {
            pageable = PageRequest.of(from, size);
        }

        List<Event> events = eventRepository.findAll(booleanBuilder, pageable).getContent();
        List<EventShortDto> eventShortDtos = createEventShortDtoWithView(events);

        if (EventSort.VIEWS.equals(sort)) {
            eventShortDtos = eventShortDtos.stream()
                    .sorted((dto1, dto2) -> dto2.getViews().compareTo(dto1.getViews()))
                    .collect(Collectors.toList());
        }

        return eventShortDtos;
    }

    public EventFullDto getEventByPublicUser(Integer id) {
        Event event = getEventOrThrow(id);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException(String.format("event id N%s", id));
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        Long view = getViews(List.of(event)).get(event.getId());
        eventFullDto.setViews(view);

        return eventFullDto;
    }


    public Event getEventOrThrow(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("event id N%s", id)));
    }

    public Map<Integer, Long> getViews(List<Event> events) {
        List<String> uris = events.stream()
                .map(Event::getId)
                .map(id -> String.format("/events/%s", id))
                .collect(Collectors.toUnmodifiableList());

        final LocalDateTime START_TIME = LocalDateTime.of(2023, 1, 1, 0, 0);
        final LocalDateTime END_TIME = LocalDateTime.now();

        List<StatsViewDto> statsViewDtos = statsClient.getStats(uris, START_TIME, END_TIME);

        return statsViewDtos.stream()
                .filter(viewDto -> viewDto.getApp().equals("ewm-service"))
                .collect(Collectors.toMap(viewDto -> {
                                    Pattern pattern = Pattern.compile("/events/([0-9]*)");
                                    Matcher matcher = pattern.matcher(viewDto.getUri());
                                    return Integer.parseInt(matcher.group(1));
                                },
                                StatsViewDto::getHits
                        )
                );
    }

    public List<EventShortDto> createEventShortDtoWithView(List<Event> events) {
        Map<Integer, Long> views = getViews(events);
        return events.stream()
                .map(event -> {
                    EventShortDto eventShortDto = EventMapper.toEventShortDto(event);
                    eventShortDto.setViews(views.getOrDefault(event.getId(), 0L));
                    return eventShortDto;
                }).collect(Collectors.toList());
    }

    private List<EventFullDto> createEventFullDtoWithView(List<Event> events) {
        Map<Integer, Long> views = getViews(events);
        return events.stream()
                .map(event -> {
                    EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
                    eventFullDto.setViews(views.getOrDefault(event.getId(), 0L));
                    return eventFullDto;
                }).collect(Collectors.toList());
    }
}
