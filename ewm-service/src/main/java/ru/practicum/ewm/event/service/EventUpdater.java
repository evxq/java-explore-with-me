package ru.practicum.ewm.event.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.location.Location;
import ru.practicum.ewm.event.location.LocationRepository;
import ru.practicum.ewm.utility.DateParser;

@Data
@RequiredArgsConstructor
abstract class EventUpdater {

    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;

    public Event updateEventFields(Event event, EventUpdateDto eventUpdateDto) {
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

}
