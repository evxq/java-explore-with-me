package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventParam;
import ru.practicum.ewm.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class EventPublicController {

    private final EventPublicService eventPublicService;

    @GetMapping
    public List<EventFullDto> getRequiredPublicEvents(HttpServletRequest request,
                                                      @RequestParam(required = false) String text,
                                                      @RequestParam(required = false) List<Long> categories,
                                                      @RequestParam(required = false) Boolean paid,
                                                      @RequestParam(required = false) String rangeStart,
                                                      @RequestParam(required = false) String rangeEnd,
                                                      @RequestParam(required = false) Boolean onlyAvailable,
                                                      @RequestParam(required = false) String sort,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
//        EventParam eventParam = new EventParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        return eventPublicService.getRequiredPublicEvents(request, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
//        return eventPublicService.getRequiredPublicEvents(request, eventParam, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPublicEvent(@PathVariable Long eventId,
                                       HttpServletRequest request) {
        return eventPublicService.getPublicEventById(eventId, request);
    }

}
