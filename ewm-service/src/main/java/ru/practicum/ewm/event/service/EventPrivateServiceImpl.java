package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
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
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setCategory(categoryRepository.getReferenceById(newEventDto.getCategory_id()));
        event.setCreatedOn(LocalDateTime.now());
        Event newEvent = eventRepository.save(event);
        log.info("Создано событие id={}", newEvent.getId());

        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateByUserDto eventUpdateByUserDto) {
        checkEventStartTime(eventUpdateByUserDto.getEventDate());
        Event existEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (existEvent.getState().equals(EventState.PUBLISHED)) {
            log.warn("Only pending or canceled events can be changed");
            throw new IllegalEventStatusException("Only pending or canceled events can be changed");
        }
        Event updEvent = eventRepository.save(existEvent);
        log.info("Обновлено событие id={}", eventId);

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
