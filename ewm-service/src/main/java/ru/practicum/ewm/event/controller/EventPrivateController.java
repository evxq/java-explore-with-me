package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByUserDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventPrivateService;

import javax.validation.Valid;
import java.util.List;

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
        return eventPrivateService.createEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable Long userId,
                                          @PathVariable Long eventId,
                                          @Valid @RequestBody EventUpdateByUserDto eventUpdateByUserDto) {
        return eventPrivateService.updateEventByUser(userId, eventId, eventUpdateByUserDto);
    }

    @GetMapping
    public List<EventFullDto> getAllEventsByUser(@PathVariable Long userId,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        return eventPrivateService.getAllEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventByUser(@PathVariable Long userId,
                                       @PathVariable Long eventId) {
        return eventPrivateService.getEventByUser(userId, eventId);
    }

}
