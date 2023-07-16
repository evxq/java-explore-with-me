package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto compilation);

    CompilationDto updateCompilation(Long compilationId, UpdateCompilationRequest compilation);

    void deleteCompilation(Long compilationId);

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilationByIdWithExistChecking(Long compilationId);

}
