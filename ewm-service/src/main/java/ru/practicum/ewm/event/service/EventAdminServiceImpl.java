package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByAdminDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventCustomRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
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
@RequiredArgsConstructor
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final EventCustomRepository eventCustomRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto eventUpdateByAdminDto) {
        Event existEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (eventUpdateByAdminDto.getEventDate() != null) {
            checkEventStartTime(eventUpdateByAdminDto.getEventDate());
            existEvent.setEventDate(DateParser.parseDate(eventUpdateByAdminDto.getEventDate()));
        }
        if (eventUpdateByAdminDto.getStateAction() != null) {
            if (eventUpdateByAdminDto.getStateAction().equals("PUBLISH_EVENT")) {
                if (existEvent.getState().equals(EventState.PENDING)) {
                    existEvent.setState(EventState.PUBLISHED);
                    existEvent.setPublishedOn(LocalDateTime.now());
                } else {
                    log.warn("Cannot publish the event because it's not in the right state: " + existEvent.getState());
                    throw new IllegalEventStatusException("Cannot publish the event because it's not in the right state: " + existEvent.getState());
                }
            } else if (eventUpdateByAdminDto.getStateAction().equals("REJECT_EVENT")) {
                if (!existEvent.getState().equals(EventState.PUBLISHED)) {
                    existEvent.setState(EventState.CANCELED);
                } else {
                    log.warn("Cannot reject the event because it's not in the right state: " + existEvent.getState());
                    throw new IllegalEventStatusException("Cannot reject the event because it's not in the right state: " + existEvent.getState());
                }
            } else {
                log.warn("Некорректный статус запроса на изменение события: {}", eventUpdateByAdminDto.getStateAction());
                throw new IllegalEventStatusException("Некорректный статус запроса на изменение события: " + eventUpdateByAdminDto.getStateAction());
            }
        }
        if (eventUpdateByAdminDto.getAnnotation() != null) {
            existEvent.setAnnotation(eventUpdateByAdminDto.getAnnotation());
        }
        if (eventUpdateByAdminDto.getCategory() != null && !eventUpdateByAdminDto.getCategory().equals(existEvent.getCategory().getId())) {
            Category updCategory = categoryRepository.getReferenceById(eventUpdateByAdminDto.getCategory());
            existEvent.setCategory(updCategory);
        }
        if (eventUpdateByAdminDto.getDescription() != null) {
            existEvent.setDescription(eventUpdateByAdminDto.getDescription());
        }
        if (eventUpdateByAdminDto.getLocation() != null) {
            Location location = eventUpdateByAdminDto.getLocation();
            existEvent.setLocation(location);
            locationRepository.save(location);
        }
        if (eventUpdateByAdminDto.getPaid() != null) {
            existEvent.setPaid(eventUpdateByAdminDto.getPaid());
        }
        if (eventUpdateByAdminDto.getParticipantLimit() != null && eventUpdateByAdminDto.getParticipantLimit() > -1) {
            existEvent.setParticipantLimit(eventUpdateByAdminDto.getParticipantLimit());
        }
        if (eventUpdateByAdminDto.getParticipantLimit() != null && eventUpdateByAdminDto.getParticipantLimit() == 0) {
            existEvent.setRequestModeration(false);
        }
        if (eventUpdateByAdminDto.getTitle() != null) {
            existEvent.setTitle(eventUpdateByAdminDto.getTitle());
        }
        Event updEvent = eventRepository.save(existEvent);
        log.info("Событие id={} обновлено администратором", eventId);

        return EventMapper.toEventFullDto(updEvent);
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
        log.info("Администратор вызвал список событий по параметрам");

        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private void checkEventStartTime(String eventDate) {
        if (DateParser.parseDate(eventDate).isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Начало события должно быть не ранее чем через 1 час от времени публикации");
            throw new WrongEventDateException("Начало события должно быть не ранее чем через 1 час от времени публикации");
        }
    }

}
