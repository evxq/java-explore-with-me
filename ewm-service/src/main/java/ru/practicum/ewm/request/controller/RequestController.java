package ru.practicum.ewm.request.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto submitRequest(@PathVariable Long userId,
                                                 @RequestParam(required = false) Long eventId) {
        return requestService.submitRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getInputRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        return requestService.getInputRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult responseToRequests(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody(required = false)
                                                                     EventRequestStatusUpdateRequest requestList) {
        return requestService.responseToRequests(userId, eventId, requestList);
    }

}
