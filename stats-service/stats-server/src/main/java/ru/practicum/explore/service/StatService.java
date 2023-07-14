package ru.practicum.explore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.util.List;

public interface StatService {

    HitDto addHit(HitDto hitDto);

    List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique);
//    List<StatsDto> getStats(String start, String end, String uri, Boolean unique) throws JsonProcessingException;

}
