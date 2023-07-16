package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.service.EventPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
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
                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        List<EventFullDto> listByParameters = eventPublicService.getRequiredPublicEvents(
                request, text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Пользователь вызвал список событий по параметрам");
        return listByParameters;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getPublicEventById(@PathVariable Long eventId,
                                           HttpServletRequest request) {
        EventFullDto publicEventById = eventPublicService.getPublicEventById(eventId, request);
        log.info("Пользователь вызвал событие id={}", eventId);
        return publicEventById;
    }

}
