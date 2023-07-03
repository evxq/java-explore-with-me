package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public HitDto addHit(@RequestBody HitDto hitDto) {
        return statService.addHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(required = false) Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

}
