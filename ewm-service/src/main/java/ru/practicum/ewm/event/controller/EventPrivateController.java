package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventPrivateService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventPrivateController {

    private final EventPrivateService eventPrivateService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        EventFullDto newEvent = eventPrivateService.createEvent(userId, newEventDto);
        log.info("Создано событие id={}", newEvent.getId());
        return newEvent;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody EventUpdateDto eventUpdateDto) {
        EventFullDto updEvent = eventPrivateService.updateEventByUser(userId, eventId, eventUpdateDto);
        log.info("Событие id={} обновлено пользователем", eventId);
        return updEvent;
    }

    @GetMapping
    public List<EventFullDto> getAllEventsByUser(@PathVariable Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<EventFullDto> eventsByUser = eventPrivateService.getAllEventsByUser(userId, from, size);
        log.info("Вызван список событий, добавленных пользователем id={}", userId);
        return eventsByUser;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        EventFullDto eventByUser = eventPrivateService.getEventByUser(userId, eventId);
        log.info("Вызвано событие id={}", eventId);
        return eventByUser;
    }

}
