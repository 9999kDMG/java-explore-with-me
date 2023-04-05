package ru.practicum.ewm_service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.event.dto.*;
import ru.practicum.ewm_service.request.RequestDto;
import ru.practicum.stats.client.StatsClient;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    //private part
    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto postEvent(@PathVariable Integer userId,
                                  @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.postEventByUser(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable Integer userId,
                                 @PathVariable Integer eventId) {
        return eventService.getEventOfUserById(userId, eventId);
    }

    @GetMapping("/users/{userId}/events")
    public List<EventFullDto> getEvents(@PathVariable Integer userId,
                                        @RequestParam(name = "from", defaultValue = "0") Integer from,
                                        @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEventsByUser(userId, from, size);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable(name = "userId") Integer userId,
                                   @PathVariable(name = "eventId") Integer eventId,
                                   @RequestBody(required = false)
                                   @Valid UpdateEventsUserRequest updateEventsUserRequest) {
        return eventService.patchEventByUser(userId, eventId, updateEventsUserRequest);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<RequestDto> getEventRequests(@PathVariable(name = "userId") Integer userId,
                                             @PathVariable(name = "eventId") Integer eventId) {
        return eventService.getRequestsByUserEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventsRequestStatusUpdateResult updateEventRequests(
            @PathVariable(name = "userId") Integer userId,
            @PathVariable(name = "eventId") Integer eventId,
            @RequestBody @Valid EventsRequestStatusUpdateRequest updateRequest) {
        return eventService.updateRequestsByUserEvent(userId, eventId, updateRequest);
    }

    //admin part
    @GetMapping("/admin/events")
    public List<EventFullDto> getAllEvents(@RequestParam(name = "users", required = false) List<Integer> users,
                                           @RequestParam(name = "states", required = false) List<EventState> states,
                                           @RequestParam(name = "categories", required = false) List<Integer> categories,

                                           @RequestParam(name = "rangeStart", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,

                                           @RequestParam(name = "rangeEnd", required = false)
                                           @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,

                                           @RequestParam(name = "from", defaultValue = "0") Integer from,
                                           @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getAllEventsWithParam(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto patchEvent(@PathVariable(name = "eventId") Integer eventId,
                                   @RequestBody(required = false)
                                   @Valid UpdateEventsAdminRequest updateEventsAdminRequest) {
        return eventService.patchEventByAdmin(eventId, updateEventsAdminRequest);
    }

    //public part
    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventShortDto(@RequestParam(name = "text", required = false) String text,
                                                @RequestParam(name = "categories", required = false) List<Integer> categories,
                                                @RequestParam(name = "paid", required = false) Boolean paid,

                                                @RequestParam(name = "rangeStart", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,

                                                @RequestParam(name = "rangeEnd", required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,

                                                @RequestParam(name = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                @RequestParam(name = "sort", required = false) EventSort sort,
                                                @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                HttpServletRequest request) {
        statsClient.hitStat(request);
        return eventService.getEventsByPublicUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventShortDto(@PathVariable(name = "id") Integer id, HttpServletRequest request) {
        statsClient.hitStat(request);
        return eventService.getEventByPublicUser(id);
    }
}
