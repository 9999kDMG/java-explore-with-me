package ru.practicum.ewm_service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.event.Event;
import ru.practicum.ewm_service.event.EventRepository;
import ru.practicum.ewm_service.event.EventService;
import ru.practicum.ewm_service.event.EventState;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;

    public RequestDto updateRequestByUser(int userId, int eventId) {
        User user = userService.getUserOrThrow(userId);
        Event event = eventService.getEventOrThrow(eventId);

        requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .ifPresent((request) -> {
                    throw new ConflictException(String
                            .format("user id N%s already made a request for the event id N%s", userId, eventId));
                });

        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException(String.format("event id N%s is not PUBLISHED", eventId));
        }

        if (event.getInitiator().getId() == userId) {
            throw new ConflictException(String.format("the user cannot make a request for his event id N%s", eventId));
        }

        if (event.getParticipantLimit() - event.getConfirmedRequests() <= 0 && event.getParticipantLimit() != 0) {
            throw new ConflictException(String.format("exceeded the limit of requests per event id N%s", eventId));
        }

        RequestStatus status = RequestStatus.PENDING;
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        Request request = Request
                .builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .status(status)
                .build();

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }

    public List<RequestDto> getAllRequestsByUser(int userId) {
        userService.getUserOrThrow(userId);

        return requestRepository.findByRequesterId(userId)
                .stream()
                .map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    public RequestDto cancelRequest(int userId, int requestId) {
        userService.getUserOrThrow(userId);
        Request request = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("request id N%s", requestId)));

        if (request.getRequester().getId() != userId) {
            throw new NotFoundException(String.format("request id N%s", requestId));
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toRequestDto(requestRepository.save(request));
    }
}
