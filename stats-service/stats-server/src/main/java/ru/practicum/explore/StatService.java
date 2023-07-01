package ru.practicum.explore;

import java.util.List;

public interface StatService {

    HitDto addHit(HitDto hitDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);

}
