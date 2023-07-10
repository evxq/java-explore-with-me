package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByAdminDto;
import ru.practicum.ewm.event.dto.EventUpdateByUserDto;

import java.util.List;

public interface EventAdminService {

    // админ может отправлять запрос на изменение события, с одним из двух статусов: PUBLISH_EVENT, REJECT_EVENT
    EventFullDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto eventUpdateByAdminDto);

    List<EventFullDto> getRequiredAdminEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                              String rangeStart, String rangeEnd, Integer from, Integer size);

}
