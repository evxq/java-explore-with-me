package ru.practicum.ewm.event.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Comment.CommentMapper;
import ru.practicum.ewm.event.Comment.CommentRepository;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.event.location.LocationRepository;
import ru.practicum.ewm.exception.IllegalEventStatusException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongEventDateException;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.utility.DateParser;
import ru.practicum.ewm.utility.PageQualifier;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class EventPrivateServiceImpl extends EventUpdater implements EventPrivateService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventPrivateServiceImpl(CategoryRepository categoryRepository, LocationRepository locationRepository,
                                   EventRepository eventRepository, UserRepository userRepository,
                                   CommentRepository commentRepository) {
        super(categoryRepository, locationRepository, commentRepository);
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        checkUserEventStartTime(newEventDto.getEventDate());
        Location location = newEventDto.getLocation();
        getLocationRepository().save(location);
        Event event = EventMapper.toEvent(newEventDto);
        event.setState(EventState.PENDING);
        event.setInitiator(userRepository.getReferenceById(userId));
        event.setCategory(getCategoryRepository().getReferenceById(newEventDto.getCategory()));
        event.setConfirmedRequests(0);
        event.setCreatedOn(LocalDateTime.now());
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        Event newEvent = eventRepository.save(event);

        return EventMapper.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateDto eventUpdateDto) {
        Event existEvent = checkEventForExist(eventId);
        if (eventUpdateDto.getEventDate() != null) {
            checkUserEventStartTime(eventUpdateDto.getEventDate());
        }
        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals("SEND_TO_REVIEW")) {
                existEvent.setState(EventState.PENDING);
            } else if (eventUpdateDto.getStateAction().equals("CANCEL_REVIEW")) {
                existEvent.setState(EventState.CANCELED);
            } else {
                log.warn("Некорректный статус запроса на изменение события {}", eventUpdateDto.getStateAction());
                throw new IllegalEventStatusException("Некорректный статус запроса на изменение события");
            }
        }
        if (existEvent.getState().equals(EventState.PUBLISHED)) {
            log.warn("Only pending or canceled events can be changed");
            throw new IllegalEventStatusException("Only pending or canceled events can be changed");
        }
        Event updEvent = updateEventFields(existEvent, eventUpdateDto);
        Event savedEvent = eventRepository.save(updEvent);

        return EventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public List<EventFullDto> getAllEventsByUser(Long userId, Integer from, Integer size) {
        return eventRepository.findAllByInitiatorId(userId, PageQualifier.definePage(from, size))
                .stream().map(this::setCommentsToEvent).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUser(Long userId, Long eventId) {
        Event event = checkEventForExist(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            log.warn("Пользователь id={} не добавлял событие id={}", userId, eventId);
            throw new NotFoundException(String.format("Пользователь id=%d не добавлял событие id=%d", userId, eventId));
        }
        return setCommentsToEvent(event);
    }

    private void checkUserEventStartTime(String eventDate) {
        if (DateParser.parseDate(eventDate).isBefore(LocalDateTime.now().plusHours(2))) {
            log.warn("Начало события должно быть не ранее чем через 2 часа");
            throw new WrongEventDateException("Начало события должно быть не ранее чем через 2 часа");
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
