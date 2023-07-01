package ru.practicum.explore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.model.HitMapper;
import ru.practicum.explore.model.Stats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final HitRepository hitRepository;
    private final StatsRepository statsRepository;

    @Override
    public HitDto addHit(HitDto hitDto) {
        Stats stat = statsRepository.findByAppAndUri(hitDto.getApp(), hitDto.getUri());
        if (stat != null) {
            stat.setHits(stat.getHits() + 1);
        } else {
            stat = Stats.builder()
                    .app(hitDto.getApp())
                    .uri(hitDto.getUri())
                    .hits(1).build();
        }
        Stats savedStats = statsRepository.save(stat);
        log.info("Обновлен Stats id={}", savedStats.getId());
        Hit hit = HitMapper.toHit(hitDto);
        hit.setStats(savedStats);
        Hit savedHit = hitRepository.save(hit);
        log.info("Создан Hit id={}", savedHit.getId());

        return HitMapper.toHitDto(savedHit);
    }

    @Override
    public List<StatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime from = LocalDateTime.from(formatter.parse(start));
        LocalDateTime to = LocalDateTime.from(formatter.parse(end));
        List<Stats> statList;

        if (uris == null && !unique) {
            statList = statsRepository.findAllByDatetimeBetween(from, to);
        }

        if (uris != null) {
            for (String uri : uris) {
                statsRepository.

            }
        }

        return null;
    }


}
