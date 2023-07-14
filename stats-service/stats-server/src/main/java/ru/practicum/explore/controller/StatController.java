package ru.practicum.explore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore.service.StatService;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto addHit(@RequestBody HitDto hitDto) {
        return statService.addHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam(required = false) String start,
                                   @RequestParam(required = false) String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(required = false) Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

}
