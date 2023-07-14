package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongParameterException;
import ru.practicum.ewm.utility.DateParser;
import ru.practicum.ewm.utility.PageQualifier;
import ru.practicum.statsclient.StatClient;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventPublicServiceImpl implements EventPublicService {

    private final EventRepository eventRepository;
    private final StatClient statClient;

    /*public List<EventFullDto> getRequiredPublicEvents(HttpServletRequest request, EventParam eventParam, Integer from, Integer size) {
        Specification<Event> spec = new EventSpecification(eventParam);
        eventRepository.findByparameters(spec, page);
    }*/

    @Override                               // ДОБАВИТЬ СТАТИСТИКУ !!!!!!!!!!
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
        PageRequest page = PageQualifier.definePage(from, size);
        Page<Event> eventList;

        if (text != null) {
            if (sort != null) {
                switch (sort) {
                    case "EVENT_DATE":
                        if (onlyAvailable) {
                            eventList = eventRepository.findAllAvailableByParamsSortByDate(text, categories, paid, start, end, page);
                            log.info("Получен список доступных событий по параметрам, сортировка по дате события");
                        } else {
                            eventList = eventRepository.findAllByParamsSortByDate(text, categories, paid, start, end, page);
                            log.info("Получен список всех событий по параметрам, сортировка по дате события");
                        }
                        break;
                    case "VIEWS":
                        if (onlyAvailable) {
                            eventList = eventRepository.findAllAvailableByParamsSortByViews(text, categories, paid, start, end, page);
                            log.info("Получен список доступных событий по параметрам, сортировка по количеству просмотров");
                        } else {
                            eventList = eventRepository.findAllByParamsSortByViews(text, categories, paid, start, end, page);
                            log.info("Получен список всех событий по параметрам, сортировка по количеству просмотров");
                        }
                        break;
                    default:
                        log.warn("Некорректный вариант сортировки");
                        throw new WrongParameterException("Некорректный вариант сортировки");
                }
            } else {        // text & categories & paid
                eventList = eventRepository.findAllByParamsWithoutSort(text, categories, paid, LocalDateTime.now(), page);
                log.info("Получен список доступных событий без сортировки");
            }
        } else if (categories != null) {
            eventList = eventRepository.findAllByCategoryIdInAndStateAndEventDateAfter(categories, EventState.PUBLISHED, LocalDateTime.now(), page);
            log.info("Получен список событий по категориям");
        } else {
            eventList = eventRepository.findAllByStateAndEventDateAfter(EventState.PUBLISHED, LocalDateTime.now(), page);
            log.info("Получен список всех событий");
        }

        List<String> eventIds = new ArrayList<>();
        for (Event event : eventList) {
            eventIds.add("/events/" + event.getId());
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
                eventIds,
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

    private void exchangeStat(Event event) {

    }

}
