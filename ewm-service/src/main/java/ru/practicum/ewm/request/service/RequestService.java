package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.EventRequestStatusUpdateResult;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto submitRequest(Long userId, Long eventId);               // подать заявку

    List<ParticipationRequestDto> getUserRequests(Long userId);                     // получение информации о заявках текущего пользователя на участие в чужих событиях

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);             // отменить заявку

    List<ParticipationRequestDto> getInputRequests(Long userId, Long eventId);      // получение информации о всех запросах на участие в событии, созданным пользователем

    EventRequestStatusUpdateResult responseToRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest requestList);

}
