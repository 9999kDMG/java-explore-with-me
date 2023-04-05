package ru.practicum.ewm_service.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto postRequest(@PathVariable(name = "userId") int userId,
                                  @RequestParam(name = "eventId") int eventId) {
        return requestService.postRequestByUser(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getAllRequests(@PathVariable(name = "userId") int userId) {
        return requestService.getAllRequestsByUser(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto patchRequest(@PathVariable(name = "userId") int userId,
                                    @PathVariable(name = "requestId") int requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
