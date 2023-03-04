package ru.practicum.compilation.service;

import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;

import java.util.Collection;

public interface CompilationService {
    CompilationDto getCompilationById(Integer compId);

    Collection<CompilationDto> getCompilationWithParam(boolean pinned, Integer from, Integer size);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilationRequest);

    void deleteCompilation(Integer compId);
}
