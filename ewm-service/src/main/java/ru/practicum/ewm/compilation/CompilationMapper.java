package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.dto.EventFullDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation) {
        Set<EventFullDto> eventSet = new HashSet<>();
        if (compilation.getEvents() != null) {
            eventSet = compilation.getEvents().stream().map(EventMapper::toEventFullDto).collect(Collectors.toSet());
        }
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(eventSet)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

}
