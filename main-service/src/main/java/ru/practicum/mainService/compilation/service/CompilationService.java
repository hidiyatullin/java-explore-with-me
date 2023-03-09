package ru.practicum.mainService.compilation.service;

import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto create(NewCompilationDto newCompilationDto);

    void deleteCompilationById(Long compId);

    CompilationDto pinnedOutCompilation(Long id);

    CompilationDto getCompilationDtoById(Long id);

    CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto);
}
