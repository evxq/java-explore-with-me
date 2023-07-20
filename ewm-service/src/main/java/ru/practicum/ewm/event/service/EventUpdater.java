package ru.practicum.ewm.event.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Comment.CommentMapper;
import ru.practicum.ewm.event.Comment.CommentRepository;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.event.location.LocationRepository;
import ru.practicum.ewm.utility.DateParser;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
abstract class EventUpdater {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final CommentRepository commentRepository;

    protected Event updateEventFields(Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null && !eventUpdateDto.getCategory().equals(event.getCategory().getId())) {
            Category updCategory = categoryRepository.getReferenceById(eventUpdateDto.getCategory());
            event.setCategory(updCategory);
        }
        if (eventUpdateDto.getDescription() != null) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getEventDate() != null) {
            event.setEventDate(DateParser.parseDate(eventUpdateDto.getEventDate()));
        }
        if (eventUpdateDto.getLocation() != null) {
            Location location = eventUpdateDto.getLocation();
            event.setLocation(location);
            locationRepository.save(location);
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null && eventUpdateDto.getParticipantLimit() > -1) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getParticipantLimit() != null && eventUpdateDto.getParticipantLimit() == 0) {
            event.setRequestModeration(false);
        }
        if (eventUpdateDto.getTitle() != null) {
            event.setTitle(eventUpdateDto.getTitle());
        }
        return event;
    }

    protected EventFullDto setCommentsToEvent(Event event) {
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        List<CommentDto> commentList =
                commentRepository.findAllByEventId(event.getId()).stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList());
        eventFullDto.setComments(commentList);

        return eventFullDto;
    }

}
