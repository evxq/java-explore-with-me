package ru.practicum.ewm.event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventCustomRepository {

    List<Event> getEventsByAdmin(List<Long> users, List<EventState> states,
                                 List<Long> categories, LocalDateTime rangeStart,
                                 LocalDateTime rangeEnd, Integer from, Integer size);

    List<Event> getEventsByUser(String text, List<Long> categories, Boolean paid,
                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                Boolean onlyAvailable, String sort, Integer from,
                                Integer size);

}
