package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongRequestParameterException;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.model.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto submitRequest(Long requesterId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (event.getInitiator().getId().equals(requesterId)) {
            log.warn("Пользователь id={} не может подать заявку на участие в своем событии id={}", requesterId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не может подать заявку на участие в своем событии id=%d", requesterId, eventId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Нельзя участвовать в неопубликованном событии id={}", eventId);
            throw new WrongRequestParameterException(String.format("Нельзя участвовать в неопубликованном событии id=%d", eventId));
        }
        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            log.warn("Достигнут лимит запросов на участие в событии id={}", eventId);
            throw new WrongRequestParameterException(String.format("Достигнут лимит запросов на участие в событии id=%d", eventId));
        }
        if (requestRepository.findByEventIdAndRequesterId(eventId, requesterId) != null) {
            log.warn("Пользователь id={} не может добавить повторный запрос на участие в событии id={}", requesterId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не может добавить повторный запрос на участие в событии id=%d", requesterId, eventId));
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(userRepository.getReferenceById(requesterId))
                .status(RequestStatus.PENDING).build();
        if (event.getRequestModeration().equals(false)) {
            request.setStatus(RequestStatus.CONFIRMED);
            int numOfParticipants = event.getConfirmedRequests();
            event.setConfirmedRequests(numOfParticipants + 1);
            eventRepository.save(event);
        }
        Request newRequest = requestRepository.save(request);
        log.info("Создана заявка на участие id={}", newRequest.getId());

        return RequestMapper.toRequestDto(newRequest);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> {
                    log.warn("User with id={} was not found", requesterId);
                    throw new NotFoundException(String.format("User with id=%d was not found", requesterId));
                });
        List<Request> requestList = requestRepository.findByRequesterId(requesterId);
        log.info("Получен список заявок на участие для пользователя id={}", requesterId);

        return requestList.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> {
                    log.warn("Request with id={} was not found", userId);
                    throw new NotFoundException(String.format("Request with id=%d was not found", userId));
                });
        request.setStatus(RequestStatus.CANCELED);
        Request canceledRequest = requestRepository.save(request);
        Event event = request.getEvent();                                           // УМЕНЬШИТЬ НА ОДИН КОЛ-ВО УЧАСТНИКОВ В EVENT?
        int numOfParticipants = event.getConfirmedRequests();
        event.setConfirmedRequests(numOfParticipants - 1);
        eventRepository.save(event);
        log.info("Пользователь id={} отменил заявку на участие в событии id={}", userId, requestId);

        return RequestMapper.toRequestDto(canceledRequest);
    }

    @Override                                   // получение информации о всех запросах на участие в событии, созданным пользователем
    public List<ParticipationRequestDto> getInputRequests(Long initiatorId, Long eventId) {


        return null;
    }

    @Override
    public EventRequestStatusUpdateResult responseToRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest requestList) {
        return null;
    }

}
