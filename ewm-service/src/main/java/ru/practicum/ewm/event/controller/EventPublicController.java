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
    public List<EventFullDto> getRequiredPublicEvents(@RequestParam String text,
                                                      @RequestParam List<Integer> categories,
                                                      @RequestParam Boolean paid,
                                                      @RequestParam String rangeStart,
                                                      @RequestParam String rangeEnd,
                                                      @RequestParam Boolean onlyAvailable,
                                                      @RequestParam String sort,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        return eventPublicService.getRequiredPublicEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPublicEvent(@PathVariable Long eventId) {
        return eventPublicService.getPublicEventById(eventId);
    }

}
