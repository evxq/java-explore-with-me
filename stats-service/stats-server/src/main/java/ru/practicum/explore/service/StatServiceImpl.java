package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.mapper.HitMapper;
import ru.practicum.explore.repository.HitRepository;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final HitRepository hitRepository;

    @Override
    public HitDto addHit(HitDto hitDto) {
        Hit savedHit = hitRepository.save(HitMapper.toHit(hitDto));
        log.info("Создан Hit id={}", savedHit.getId());

        return HitMapper.toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        List<Stats> statList = null;
        if (uris == null && !unique) {                                                                                  // ТЕСТ 1.2
            statList = hitRepository.findStatsByDatetimeBetween(formatDate(start), formatDate(end));                    // только начало и конец, unique=false
        }
        if (uris != null && (unique == null || !unique)) {                                                              // ТЕСТЫ 1.1 / 2.1 / 3.1 / 3.2
            statList = hitRepository.findStatsByDatetimeBetweenAndUriIn(formatDate(start), formatDate(end), uris);      // начало и конец, uri != null, unique=false
        }
        if (uris == null && unique) {
            statList = hitRepository.findStatsByDistinctIp(formatDate(start), formatDate(end));                         // начало и конец, unique=true
        }
        if (uris != null && Boolean.TRUE.equals(unique)) {                                                              // ТЕСТ 2.2
            statList = hitRepository.findStatsByUriDistinctIp(formatDate(start), formatDate(end), uris);                // все параметры
        }
        return statList.stream().map(HitMapper::toStatsDto).collect(Collectors.toList());
    }

    private LocalDateTime formatDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.from(formatter.parse(date));
    }

}
