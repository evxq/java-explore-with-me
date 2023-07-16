package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongParameterException;
import ru.practicum.ewm.utility.DateParser;
import ru.practicum.statsclient.StatClient;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final EventCustomRepository eventCustomRepository;
    private final StatClient statClient;

    @Override
    public List<EventFullDto> getRequiredPublicEvents(HttpServletRequest request, String text, List<Long> categories, Boolean paid,
                                                      String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                      String sort, Integer from, Integer size) {
        LocalDateTime start = null;
        LocalDateTime end = null;
        if (rangeStart != null) {
            start = DateParser.parseDate(rangeStart);
        }
        if (rangeEnd != null) {
            end = DateParser.parseDate(rangeEnd);
            if (start != null && start.isAfter(end)) {
                log.warn("Дата rangeStart указана позже даты rangeEnd");
                throw new WrongParameterException("Дата rangeStart не может быть позже даты rangeEnd");
            }
        }
        List<Event> eventList = eventCustomRepository.getEventsByUser(text, categories, paid, start, end, onlyAvailable, sort, from, size);
        List<String> events = new ArrayList<>();
        for (Event event : eventList) {
            events.add("/events/" + event.getId());
        }
        if (start == null) {
            start = LocalDateTime.now().minusYears(1);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        ResponseEntity<List<StatsDto>> response = statClient.getStats(
                start.toString(),
                end.toString(),
                events,
                true);
        if (response.getBody() != null && response.getBody().size() > 0) {
            for (StatsDto statsDto : response.getBody()) {
                Long eventId = Long.parseLong(statsDto.getUri().split("events/")[1]);
                for (Event event : eventList) {
                    if (event.getId().equals(eventId)) {
                        event.setViews(statsDto.getHits());
                    }
                }
            }
        }
        if (sort != null && sort.equals("VIEWS")) {
            eventList = eventList.stream()
                    .sorted(Comparator.comparing(Event::getViews, Comparator.naturalOrder()))
                    .collect(Collectors.toList());
        }
        statClient.addHit(HitDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(String.valueOf(LocalDateTime.now()))
                .build());
        return eventList.stream().map(EventMapper::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getPublicEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED)
                .orElseThrow(() -> {
                    log.warn("Published event with id={} was not found", eventId);
                    return new NotFoundException(String.format("Published event with id=%d was not found", eventId));
                });
        ResponseEntity<List<StatsDto>> response = statClient.getStats(
                event.getPublishedOn().toString(),
                LocalDateTime.now().toString(),
                List.of(request.getRequestURI()),
                true);
        if (response.getBody() != null && response.getBody().size() > 0) {
            event.setViews(response.getBody().get(0).getHits());
        }
        statClient.addHit(HitDto.builder()
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(String.valueOf(LocalDateTime.now()))
                .build());
        return EventMapper.toEventFullDto(event);
    }

}
