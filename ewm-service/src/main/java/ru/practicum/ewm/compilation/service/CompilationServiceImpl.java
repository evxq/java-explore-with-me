package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.CompilationMapper;
import ru.practicum.ewm.compilation.CompilationRepository;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.utility.PageQualifier;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto compilationToCreate) {
        Compilation compilation = CompilationMapper.toCompilation(compilationToCreate);
        if (compilationToCreate.getEvents() != null) {
            Set<Event> newEventSet = eventRepository.findAllByIdIn(compilationToCreate.getEvents());
            compilation.setEvents(newEventSet);
        }
        if (compilationToCreate.getPinned() == null) {
            compilation.setPinned(false);
        }
        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Создана подборка id={}", newCompilation.getId());

        return CompilationMapper.toCompilationDto(newCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest compilationToUpdate) {
        Compilation compilation = checkCompilationForExist(compId);
        if (compilationToUpdate.getEvents() != null) {
            Set<Event> newEventSet = eventRepository.findAllByIdIn(compilationToUpdate.getEvents());
            compilation.setEvents(newEventSet);
        }
        if (compilation.getPinned() != null) {
            compilation.setPinned(compilation.getPinned());
        }
        if (compilation.getTitle() != null) {
            compilation.setTitle(compilation.getTitle());
        }
        Compilation updCompilation = compilationRepository.save(compilation);
        log.info("Обновлена подборка id={}", compId);

        return CompilationMapper.toCompilationDto(updCompilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        checkCompilationForExist(compId);
        compilationRepository.deleteById(compId);
        log.info("Удалена подборка id={}", compId);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compPage;
        if (pinned == null) {
            compPage = compilationRepository.findAll(PageQualifier.definePage(from, size));
            log.info("Вызван список всех подборок событий");
        } else {
            compPage = compilationRepository.findByPinned(pinned, PageQualifier.definePage(from, size));
            log.info("Вызван список подборок событий, pinned={}", pinned);
        }
        return compPage.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = checkCompilationForExist(compId);
        log.info("Вызвана подборка id={}", compId);

        return CompilationMapper.toCompilationDto(compilation);
    }

    private Compilation checkCompilationForExist(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.warn("Compilation with id={} was not found", compId);
                    throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
                });
    }

}
