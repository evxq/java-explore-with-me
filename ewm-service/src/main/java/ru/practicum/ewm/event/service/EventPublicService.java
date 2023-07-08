package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;

import java.util.List;

public interface EventPublicService {

    List<EventFullDto> getRequiredPublicEvents(String text, List<Integer> categories, Boolean paid,
                                               String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                               String sort, Integer from, Integer size);

    EventFullDto getPublicEventById(Long eventId);

}
