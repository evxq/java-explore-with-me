package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByUserDto;
import ru.practicum.ewm.event.dto.NewEventDto;

import java.util.List;

public interface EventPrivateService {

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByUser(Long userId, Long eventId, EventUpdateByUserDto eventUpdateByUserDto);

    List<EventFullDto> getAllEventsByUser(Long userId, Integer from, Integer size);

    EventFullDto getEventByUser(Long userId, Long eventId);

}
