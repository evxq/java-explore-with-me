package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class StatController {

    private final StatService statService;

    @PostMapping("/hit")
    public HitDto addHit(@RequestBody HitDto hitDto) {
        return statService.addHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(//@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                   //@RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end
                                   @RequestParam String start,
                                   @RequestParam String end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") Boolean unique) {
        return statService.getStats(start, end, uris, unique);
    }

}
