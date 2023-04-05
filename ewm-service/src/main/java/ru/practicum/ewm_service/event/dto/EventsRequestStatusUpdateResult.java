package ru.practicum.ewm_service.event.dto;

import lombok.*;
import ru.practicum.ewm_service.request.RequestDto;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor

@Setter
@Getter

@Builder
public class EventsRequestStatusUpdateResult {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
