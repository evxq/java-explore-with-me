package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
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

        return CompilationMapper.toCompilationDto(newCompilation);
    }

    @Override
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilationToUpdate) {
        Compilation existCompilation = getCompilationByIdWithExistChecking(compilationId);
        if (compilationToUpdate.getEvents() != null) {
            Set<Event> newEventSet = eventRepository.findAllByIdIn(compilationToUpdate.getEvents());
            existCompilation.setEvents(newEventSet);
        }
        if (existCompilation.getPinned() != null) {
            existCompilation.setPinned(existCompilation.getPinned());
        }
        if (existCompilation.getTitle() != null) {
            existCompilation.setTitle(existCompilation.getTitle());
        }
        Compilation updCompilation = compilationRepository.save(existCompilation);

        return CompilationMapper.toCompilationDto(updCompilation);
    }

    @Override
    public void deleteCompilation(Long compilationId) {
        getCompilationByIdWithExistChecking(compilationId);
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        Page<Compilation> compilationsPage;
        if (pinned == null) {
            compilationsPage = compilationRepository.findAll(PageQualifier.definePage(from, size));
        } else {
            compilationsPage = compilationRepository.findByPinned(pinned, PageQualifier.definePage(from, size));
        }
        return compilationsPage.stream().map(CompilationMapper::toCompilationDto).collect(Collectors.toList());
    }

    @Override
    public Compilation getCompilationByIdWithExistChecking(Long compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> {
                    log.warn("Compilation with id={} was not found", compilationId);
                    throw new NotFoundException(String.format("Compilation with id=%d was not found", compilationId));
                });
    }

}
