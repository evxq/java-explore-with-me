package ru.practicum.ewm.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto submitRequest(@PathVariable Long userId,
                                                 @RequestParam(required = false) Long eventId) {
        ParticipationRequestDto newRequest = requestService.submitRequest(userId, eventId);
        log.info("Создана заявка на участие id={}", newRequest.getId());
        return newRequest;
    }

    @PatchMapping("/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        ParticipationRequestDto canceledRequest = requestService.cancelRequest(userId, requestId);
        log.info("Пользователь id={} отменил заявку на участие в событии id={}", userId, requestId);
        return canceledRequest;
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        List<ParticipationRequestDto> userRequests = requestService.getUserRequests(userId);
        log.info("Получен список заявок на участие для пользователя id={}", userId);
        return userRequests;
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getInputRequests(@PathVariable Long userId,
                                                          @PathVariable Long eventId) {
        List<ParticipationRequestDto> inputRequests = requestService.getInputRequests(userId, eventId);
        log.info("Получен список заявок на участие в событии id={}, созданного пользователем id={}", eventId, userId);
        return inputRequests;
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult responseToRequests(@PathVariable Long userId,
                                                             @PathVariable Long eventId,
                                                             @RequestBody(required = false)
                                                                     EventRequestStatusUpdateRequest requestList) {
        EventRequestStatusUpdateResult response = requestService.responseToRequests(userId, eventId, requestList);
        log.info("Пользователь id={} ответил на заявки к событию id={}", userId, eventId);
        return response;
    }

}
