package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongParameterException;
import ru.practicum.ewm.exception.WrongRequestParameterException;
import ru.practicum.ewm.request.RequestMapper;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        if (eventId == null) {
            log.warn("Параметр eventId равен null");
            throw new WrongParameterException("Параметр eventId равен null");
        }
        Event event = checkEventForExist(eventId);
        /*Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });*/
        if (event.getInitiator().getId().equals(requesterId)) {
            log.warn("Пользователь id={} не может подать заявку на участие в своем событии id={}", requesterId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не может подать заявку на участие в своем событии id=%d", requesterId, eventId));
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Нельзя участвовать в неопубликованном событии id={}", eventId);
            throw new WrongRequestParameterException(String.format("Нельзя участвовать в неопубликованном событии id=%d", eventId));
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            // ПОСЛЕ ПРЕВЫШЕНИЯ ЛИМИТА ДОЛЖЕН ВОЗВРАЩАТЬ REJECTED????
            Request rejectedRequest = Request.builder()
                    .created(LocalDateTime.now().withNano(0))
                    .event(event)
                    .requester(userRepository.getReferenceById(requesterId))
                    .status(RequestStatus.REJECTED).build();
            requestRepository.save(rejectedRequest);

            log.warn("Достигнут лимит запросов на участие в событии id={}", eventId);
            throw new WrongRequestParameterException(String.format("Достигнут лимит запросов на участие в событии id=%d", eventId));
        }
        if (requestRepository.findByEventIdAndRequesterId(eventId, requesterId) != null) {
            log.warn("Пользователь id={} не может добавить повторный запрос на участие в событии id={}", requesterId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не может добавить повторный запрос на участие в событии id=%d", requesterId, eventId));
        }
        int numOfParticipants = event.getConfirmedRequests();
        event.setConfirmedRequests(numOfParticipants + 1);
        Request request = Request.builder()
                .created(LocalDateTime.now().withNano(0))
                .event(event)
                .requester(userRepository.getReferenceById(requesterId))
                .status(RequestStatus.PENDING).build();
        if (event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }
        eventRepository.save(event);
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
        Event event = request.getEvent();
        int numOfParticipants = event.getConfirmedRequests();
        event.setConfirmedRequests(numOfParticipants - 1);
        eventRepository.save(event);
        log.info("Пользователь id={} отменил заявку на участие в событии id={}", userId, requestId);

        return RequestMapper.toRequestDto(canceledRequest);
    }

    @Override                      // получение информации о всех запросах на участие в событии, созданным пользователем
    public List<ParticipationRequestDto> getInputRequests(Long initiatorId, Long eventId) {
        Event event = checkEventForExist(eventId);
        /*Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });*/
        if (!event.getInitiator().getId().equals(initiatorId)) {
            log.warn("Пользователь id={} не является инициатором события id={}", initiatorId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не является инициатором события id=%d", initiatorId, eventId));
        }
        List<Request> requestList = requestRepository.findRequestsByInitiator(eventId);
        log.info("Получен список заявок на участие в событии id={}, созданного пользователем id={}", eventId, initiatorId);

        return requestList.stream().map(RequestMapper::toRequestDto).collect(Collectors.toList());
    }

    @Override                                           // НУЖНО ПОСТАВИТЬ ЗАЯВКАМ ИЗ requestList СТАТУС ИЗ requestList
    public EventRequestStatusUpdateResult responseToRequests(Long userId, Long eventId, EventRequestStatusUpdateRequest requestStatusList) {
        if (requestStatusList == null) {
            log.warn("Некорректные данные");
            throw new WrongRequestParameterException("Некорректные данные");
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        Event event = checkEventForExist(eventId);
        /*Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });*/
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("Пользователь id={} не является инициатором события id={}", userId, eventId);
            throw new WrongRequestParameterException(String.format("Пользователь id=%d не является инициатором события id=%d", userId, eventId));
        }
        Integer originalConfirmedRequests = event.getConfirmedRequests();
        RequestStatus requestStatus = RequestStatus.valueOf(requestStatusList.getStatus());
        List<Long> idList = requestStatusList.getRequestIds();
        List<Request> requestList = requestRepository.findByIdIn(idList);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            for (Request request : requestList) {
                if (request.getStatus().equals(RequestStatus.PENDING)) {
                    request.setStatus(requestStatus);
                    Request savedRequest = requestRepository.save(request);
                    if (requestStatus.equals(RequestStatus.CONFIRMED)) {
                        confirmedRequests.add(RequestMapper.toRequestDto(savedRequest));
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    } else {
                        rejectedRequests.add(RequestMapper.toRequestDto(savedRequest));
                    }
                } else {
                    log.warn("Заявка id={} не находится в состоянии ожидания", request.getId());
                    throw new WrongRequestParameterException(String.format("Заявка id=%d не находится в состоянии ожидания", request.getId()));
                }
            }
        } else {
            for (Request request : requestList) {
                if (request.getStatus().equals(RequestStatus.PENDING)) {
                    if (requestStatus.equals(RequestStatus.CONFIRMED)) {
                        if (event.getParticipantLimit() - event.getConfirmedRequests() > 0) {
                            request.setStatus(requestStatus);
                            Request confirmedRequest = requestRepository.save(request);
                            confirmedRequests.add(RequestMapper.toRequestDto(confirmedRequest));
                            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        } else {
                            log.warn("Для события id={} превышен лимит заявок", eventId);
                            throw new WrongRequestParameterException(String.format("Для события id=%d превышен лимит заявок", eventId));
                        }
                    } else {
                        request.setStatus(requestStatus);
                        Request rejectedRequest = requestRepository.save(request);
                        rejectedRequests.add(RequestMapper.toRequestDto(rejectedRequest));
                    }
                } else {
                    log.warn("Заявка id={} не находится в состоянии ожидания", request.getId());
                    throw new WrongRequestParameterException(String.format("Заявка id=%d не находится в состоянии ожидания", request.getId()));
                }
            }

        }
        if (!originalConfirmedRequests.equals(event.getConfirmedRequests())) {
            eventRepository.save(event);
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests).build();
    }

    private Event checkEventForExist(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
    }

}
