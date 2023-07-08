package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;

import java.util.List;

public interface EventService {

    EventFullDto addCategory(EventFullDto categoryDto);

    EventFullDto updateCategory(EventFullDto eventFullDto, Long catId);

    List<EventFullDto> getAllCategories (Integer from, Integer size);

    EventFullDto getCategoryById(Long catId);

    void deleteCategory(Long catId);

}
