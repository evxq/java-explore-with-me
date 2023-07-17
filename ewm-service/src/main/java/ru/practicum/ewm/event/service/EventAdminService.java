package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;

import java.util.List;

public interface EventAdminService {

    EventFullDto updateEventByAdmin(Long eventId, EventUpdateDto eventUpdateDto);

    List<EventFullDto> getRequiredAdminEvents(List<Long> users, List<String> states, List<Long> categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size);

}
