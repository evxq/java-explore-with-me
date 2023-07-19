package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.Comment.CommentRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.location.LocationRepository;
import ru.practicum.ewm.exception.IllegalEventStatusException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongEventDateException;
import ru.practicum.ewm.utility.DateParser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EventAdminServiceImpl extends EventUpdater implements EventAdminService {

    private final EventRepository eventRepository;
    private final EventCustomRepository eventCustomRepository;

    public EventAdminServiceImpl(CategoryRepository categoryRepository, LocationRepository locationRepository,
                                 EventCustomRepository eventCustomRepository, EventRepository eventRepository,
                                 CommentRepository commentRepository) {
        super(categoryRepository, locationRepository, commentRepository);
        this.eventRepository = eventRepository;
        this.eventCustomRepository = eventCustomRepository;
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventUpdateDto) {
        Event existEvent = checkEventForExist(eventId);
        if (eventUpdateDto.getEventDate() != null) {
            checkAdminEventStartTime(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals("PUBLISH_EVENT")) {
                if (existEvent.getState().equals(EventState.PENDING)) {
                    existEvent.setState(EventState.PUBLISHED);
                    existEvent.setPublishedOn(LocalDateTime.now());
                } else {
                    log.warn("Cannot publish the event because it's not in the right state: " + existEvent.getState());
                    throw new IllegalEventStatusException("Cannot publish the event because it's not in the right state: " + existEvent.getState());
                }
            } else if (eventUpdateDto.getStateAction().equals("REJECT_EVENT")) {
                if (!existEvent.getState().equals(EventState.PUBLISHED)) {
                    existEvent.setState(EventState.CANCELED);
                } else {
                    log.warn("Cannot reject the event because it's not in the right state: " + existEvent.getState());
                    throw new IllegalEventStatusException("Cannot reject the event because it's not in the right state: " + existEvent.getState());
                }
            } else {
                log.warn("Некорректный статус запроса на изменение события: {}", eventUpdateDto.getStateAction());
                throw new IllegalEventStatusException("Некорректный статус запроса на изменение события: " + eventUpdateDto.getStateAction());
            }
        }
        Event updEvent = this.updateEventFields(existEvent, eventUpdateDto);
        Event savedEvent = eventRepository.save(updEvent);

        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventFullDto> getRequiredAdminEvents(List<Long> users, List<String> states,
                                                     List<Long> categories, String rangeStart,
                                                     String rangeEnd, Integer from, Integer size) {
        List<EventState> stateList = null;
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (states != null) {
            stateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        }
        if (rangeStart != null) {
            start = DateParser.parseDate(rangeStart);
        }
        if (rangeEnd != null) {
            end = DateParser.parseDate(rangeEnd);
        }
        List<Event> eventList = eventCustomRepository.getEventsByAdmin(users, stateList, categories, start, end, from, size);

        return eventList.stream().map(this::setCommentsToEvent).collect(Collectors.toList());
    }

    private void checkAdminEventStartTime(String eventDate) {
        if (DateParser.parseDate(eventDate).isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Начало события должно быть не ранее чем через 1 час от времени публикации");
            throw new WrongEventDateException("Начало события должно быть не ранее чем через 1 час от времени публикации");
        }
    }

    private Event checkEventForExist(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
    }

}
