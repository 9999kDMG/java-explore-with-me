package ru.practicum.ewm_service.event.dto;

import lombok.*;
import ru.practicum.ewm_service.request.RequestStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class EventsRequestStatusUpdateRequest {
    private List<Integer> requestIds;
    private RequestStatus status;
}
