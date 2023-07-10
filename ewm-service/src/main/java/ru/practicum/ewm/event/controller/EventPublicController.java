package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventPublicService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventPublicService eventPublicService;

    @GetMapping
    public List<EventFullDto> getRequiredPublicEvents(@RequestParam(required = false) String text,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(required = false) Boolean onlyAvailable,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return eventPublicService.getRequiredPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPublicEvent(@PathVariable Long eventId) {
        return eventPublicService.getPublicEventById(eventId);
    }

}
