package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByAdminDto;
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
public class EventAdminServiceImpl implements EventAdminService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // админ может отправлять запрос на изменение события, с одним из двух статусов: PUBLISH_EVENT, REJECT_EVENT
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto eventUpdateByAdminDto) {
        Event existEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        if (eventUpdateByAdminDto.getStateAction().equals("PUBLISH_EVENT")) {
            if (existEvent.getState().equals(EventState.PENDING)) {
                checkEventStartTime(eventUpdateByAdminDto.getEventDate());
                existEvent.setEventDate(DateParser.parseDate(eventUpdateByAdminDto.getEventDate()));
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
        existEvent.setAnnotation(eventUpdateByAdminDto.getAnnotation());
        Category updCategory = categoryRepository.getReferenceById(eventUpdateByAdminDto.getCategory_id());
        existEvent.setCategory(updCategory);
        existEvent.setDescription(eventUpdateByAdminDto.getDescription());
        existEvent.setLocation(eventUpdateByAdminDto.getLocation());
        existEvent.setPaid(eventUpdateByAdminDto.getPaid());
        existEvent.setParticipantLimit(eventUpdateByAdminDto.getParticipantLimit());
        if (eventUpdateByAdminDto.getParticipantLimit() == 0) {              // если лимита нет, то модерация не нужна?
            existEvent.setRequestModeration(false);
        }
        existEvent.setTitle(eventUpdateByAdminDto.getTitle());
        Event updEvent = eventRepository.save(existEvent);
        log.info("Событие id={} обновлено администратором", eventId);

        return EventMapper.toEventFullDto(updEvent);
    }

    @Override
    public List<EventFullDto> getRequiredAdminEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                                     String rangeStart, String rangeEnd, Integer from, Integer size) {
        List<EventState> stateList = states.stream().map(EventState::valueOf).collect(Collectors.toList());
        LocalDateTime start = DateParser.parseDate(rangeStart);
        LocalDateTime end = DateParser.parseDate(rangeEnd);
        PageRequest page = PageDefinition.definePage(from, size);
        Page<Event> eventList;
        if (users != null) {
            eventList = eventRepository.findAllByParametersForAdmin(users, stateList, categories, start, end, page);
            log.info("Администратор запросил список всех событий по параметрам");
        } else {
            eventList = eventRepository.findAll(page);
            log.info("Администратор запросил список всех событий");
        }
        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    private void checkEventStartTime(String eventDate) {
        if (DateParser.parseDate(eventDate).isBefore(LocalDateTime.now().plusHours(1))) {
            log.warn("Начало события должно быть не ранее чем за час от времени публикации");
            throw new WrongEventDateException("Начало события должно быть не ранее чем за час от времени публикации");
        }
    }

}
