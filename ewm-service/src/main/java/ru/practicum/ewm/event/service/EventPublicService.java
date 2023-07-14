package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventPublicService {

    List<EventFullDto> getRequiredPublicEvents(HttpServletRequest request, String text, List<Long> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size);
//    List<EventFullDto> getRequiredPublicEvents(HttpServletRequest request, EventParam eventParam, Integer from, Integer size);

    EventFullDto getPublicEventById(Long eventId, HttpServletRequest request);

}
