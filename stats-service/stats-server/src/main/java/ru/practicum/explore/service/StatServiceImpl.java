package ru.practicum.explore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explore.exception.WrongParameterException;
import ru.practicum.explore.mapper.HitMapper;
import ru.practicum.explore.model.Hit;
import ru.practicum.explore.model.Stats;
import ru.practicum.explore.repository.HitRepository;
import ru.practicum.statsdto.dto.HitDto;
import ru.practicum.statsdto.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        if (start == null || end == null) {
            log.warn("Не указана дата начала или конца диапазона");
            throw new WrongParameterException("Не указана дата начала или конца диапазона");
        }
        LocalDateTime startDate = formatDate(start);
        LocalDateTime endDate = formatDate(end);
        if (endDate.isBefore(startDate)) {
            log.warn("Дата начала диапазона указана позже даты конца диапазона");
            throw new WrongParameterException("Дата начала диапазона не может быть позже даты конца диапазона");
        }
        List<String> urisList = checkUriFormat(uris);
        List<Stats> statList = new ArrayList<>();
        if (urisList.isEmpty() && !unique) {
            statList = hitRepository.findStatsByDatetimeBetween(startDate, endDate);                    // только начало и конец, unique=false
        }
        if (!urisList.isEmpty() && (unique == null || !unique)) {
            statList = hitRepository.findStatsByDatetimeBetweenAndUriIn(startDate, endDate, urisList);  // начало и конец, uri != null, unique=false
        }
        if (urisList.isEmpty() && Boolean.TRUE.equals(unique)) {
            statList = hitRepository.findStatsByDistinctIp(startDate, endDate);                         // начало и конец, unique=true
        }
        if (!urisList.isEmpty() && Boolean.TRUE.equals(unique)) {
            statList = hitRepository.findStatsByUriDistinctIp(startDate, endDate, urisList);            // все параметры
        }
        return statList.stream().map(HitMapper::toStatsDto).collect(Collectors.toList());
    }

    private LocalDateTime formatDate(String date) {
        String fixDate = date.replace("T", " ").substring(0, 19);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.from(formatter.parse(fixDate));
    }

    private List<String> checkUriFormat(List<String> uris) {
        List<String> urisList = new ArrayList<>();

        if (!uris.isEmpty() && uris.get(0).contains("[")) {
            if (uris.size() == 1) {
                String cut = uris.get(0).substring(1, uris.get(0).length() - 1);
                urisList.add(cut);
            } else if (uris.size() == 2) {
                String first = uris.get(0).substring(1);
                String last = uris.get(uris.size() - 1);
                String lastCut = last.substring(0, last.length() - 1);
                urisList.add(first);
                urisList.add(lastCut);
            } else {
                String first = uris.get(0).substring(1);
                String last = uris.get(uris.size() - 1);
                String lastCut = last.substring(0, last.length() - 1);
                for (int i = 1; i < uris.size() - 2; i++) {
                    urisList.add(uris.get(i));
                }
                urisList.add(0, first);
                urisList.add(lastCut);
            }
        } else {
            urisList = uris;
        }
        return urisList;
    }

}
