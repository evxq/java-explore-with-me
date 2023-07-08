package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.PageDefinition;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventFullDto addCategory(EventFullDto eventFullDto) {
        Event event = EventMapper.toCategory(eventFullDto);
        Event newEvent = eventRepository.save(event);
        log.info("Создана категория id={}", newEvent.getId());

        return EventMapper.toCategoryDto(newEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateCategory(EventFullDto eventFullDto, Long catId) {
        Event existEvent = eventRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
                    throw new NotFoundException(String.format("Category with id=%d was not found", catId));
                });
        if (eventFullDto.getName() != null) {
            existEvent.setName(eventFullDto.getName());
        }
        Event updEvent = eventRepository.save(existEvent);
        log.info("Обновлена категория id={}", catId);

        return EventMapper.toCategoryDto(updEvent);
    }

    @Override
    public void deleteCategory(Long catId) {                    // УДАЛЕНИЕ ВОЗМОЖНО ТОЛЬКО ЕСЛИ С КАТЕГОРИЕЙ НЕ СВЯЗАНО НИ ОДНОГО СОБЫТИЯ
        eventRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
                    return new NotFoundException(String.format("Category  with id=%d was not found", catId));
                });
        eventRepository.deleteById(catId);
        log.info("Удалена категория id={}", catId);
    }

    @Override
    public List<EventFullDto> getAllCategories(Integer from, Integer size) {
        log.info("Вызван список всех категорий");

        return eventRepository.findAll(PageDefinition.definePage(from, size))
                .stream().map(EventMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getCategoryById(Long catId) {
        log.info("Вызвана категория id={}", catId);
        Event event = eventRepository.findById(catId)
                .orElseThrow(() -> {
                    log.warn("Category with id={} was not found", catId);
                    return new NotFoundException(String.format("Category with id=%d was not found", catId));
                });
        return EventMapper.toCategoryDto(event);
    }

}
