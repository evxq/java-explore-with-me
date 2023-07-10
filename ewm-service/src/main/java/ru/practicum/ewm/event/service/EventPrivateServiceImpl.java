package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByUserDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.IllegalEventStatusException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongEventDateException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.utility.DateParser;
import ru.practicum.ewm.utility.PageDefinition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventPrivateServiceImpl implements EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkEventStartTime(newEventDto.getEventDate());
        Event event = EventMapper.toEvent(newEventDto);
        event.setState(EventState.PENDING);
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setCategory(categoryRepository.getReferenceById(newEventDto.getCategory_id()));
        event.setCreatedOn(LocalDateTime.now());
        if (newEventDto.getParticipantLimit() == 0) {               // если лимита нет, то модерация не нужна?
            event.setRequestModeration(false);
        }
        Event newEvent = eventRepository.save(event);
        log.info("Создано событие id={}", newEvent.getId());

        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateByUserDto eventUpdateByUserDto) {
        checkEventStartTime(eventUpdateByUserDto.getEventDate());       // дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
        Event existEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (eventUpdateByUserDto.getStateAction().equals("SEND_TO_REVIEW")) {
            existEvent.setState(EventState.PENDING);
        } else if (eventUpdateByUserDto.getStateAction().equals("CANCEL_REVIEW")) {
            existEvent.setState(EventState.CANCELED);
        } else {
            log.warn("Некорректный статус запроса на изменение события {}", eventUpdateByUserDto.getStateAction());
            throw new IllegalEventStatusException("Некорректный статус запроса на изменение события");
        }
        if (existEvent.getState().equals(EventState.PUBLISHED)) {       // изменить можно только отмененные события или события в состоянии ожидания модерации
            log.warn("Only pending or canceled events can be changed");
            throw new IllegalEventStatusException("Only pending or canceled events can be changed");
        }
        if (eventUpdateByUserDto.getAnnotation() != null) {
            existEvent.setAnnotation(eventUpdateByUserDto.getAnnotation());
        }
        if (!eventUpdateByUserDto.getCategory_id().equals(existEvent.getCategory().getId())) {
            Category updCategory = categoryRepository.getReferenceById(eventUpdateByUserDto.getCategory_id());
            existEvent.setCategory(updCategory);
        }
        if (eventUpdateByUserDto.getDescription() != null) {
            existEvent.setDescription(eventUpdateByUserDto.getDescription());
        }
        existEvent.setEventDate(DateParser.parseDate(eventUpdateByUserDto.getEventDate()));
        if (eventUpdateByUserDto.getLocation() != null) {
            existEvent.setLocation(eventUpdateByUserDto.getLocation());
        }
        if (eventUpdateByUserDto.getPaid() != null) {
            existEvent.setPaid(eventUpdateByUserDto.getPaid());
        }
        if (eventUpdateByUserDto.getParticipantLimit() != null || eventUpdateByUserDto.getParticipantLimit() > -1) {
            existEvent.setParticipantLimit(eventUpdateByUserDto.getParticipantLimit());
        }
        if (eventUpdateByUserDto.getParticipantLimit() == 0) {              // если лимита нет, то модерация не нужна?
            existEvent.setRequestModeration(false);
        }
        if (eventUpdateByUserDto.getTitle() != null) {
            existEvent.setTitle(eventUpdateByUserDto.getTitle());
        }
        Event updEvent = eventRepository.save(existEvent);
        log.info("Событие id={} обновлено пользователем", eventId);

        return EventMapper.toEventFullDto(updEvent);
    }

    @Override
    public List<EventFullDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        log.info("Вызван список событий, добавленных пользователем id={}", userId);

        return eventRepository.findAllByInitiatorId(userId, PageDefinition.definePage(from, size))
                .stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    return new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("");
            throw new NotFoundException(String.format("Пользователь id=%d не добавлял событие id=%d", userId, eventId));
        }
        log.info("Вызвано событие id={}", eventId);

        return EventMapper.toEventFullDto(event);
    }

    private void checkEventStartTime(String eventDate) {
        if (DateParser.parseDate(eventDate).isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Начало события должно быть не ранее чем через 2 часа");
            throw new WrongEventDateException("Начало события должно быть не ранее чем через 2 часа");
        }
    }

}
