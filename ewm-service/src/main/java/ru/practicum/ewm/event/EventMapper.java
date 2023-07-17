package ru.practicum.ewm.event;

import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.user.UserMapper;
import ru.practicum.ewm.utility.DateParser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EventMapper {

    public static EventFullDto toEventFullDto(Event event) {
        String published = null;
        if (event.getPublishedOn() != null) {
            published = event.getPublishedOn().toString();
        }
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().toString())
                .description(event.getDescription())
                .eventDate(dateToString(event.getEventDate()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(published)
                .requestModeration(event.getRequestModeration())
                .state(event.getState().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public static Set<EventFullDto> toEventFullDto(Collection<Event> events) {
        Set<EventFullDto> eventSet = new HashSet<>();
        if (events != null) {
            eventSet = events.stream().map(EventMapper::toEventFullDto).collect(Collectors.toSet());
        }
        return eventSet;
    }

    public static Event toEvent(NewEventDto newEventDto) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .eventDate(DateParser.parseDate(newEventDto.getEventDate()))
                .location(newEventDto.getLocation())
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .title(newEventDto.getTitle())
                .build();
    }

    public static String dateToString(LocalDateTime date) {
        return date.toString().replace("T", " ");
    }

}
