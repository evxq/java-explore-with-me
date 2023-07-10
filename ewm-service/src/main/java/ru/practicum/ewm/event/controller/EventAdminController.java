package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventUpdateByAdminDto;
import ru.practicum.ewm.event.service.EventAdminService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventAdminController {

    private final EventAdminService eventAdminService;

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @RequestBody EventUpdateByAdminDto eventUpdateByAdminDtoDto) {
        return eventAdminService.updateEventByAdmin(eventId, eventUpdateByAdminDtoDto);
    }

    @GetMapping
    public List<EventFullDto> getRequiredAdminEvents(@RequestParam(required = false) List<Integer> users,
                                                     @RequestParam(required = false) List<String> states,
                                                     @RequestParam(required = false) List<Integer> categories,
                                                     @RequestParam(required = false) String rangeStart,
                                                     @RequestParam(required = false) String rangeEnd,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return eventAdminService.getRequiredAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

}
