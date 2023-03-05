package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.error.RequestError;
import ru.practicum.event.service.EventService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    public CompilationDto getCompilationById(Integer compId) {
        log.info("Запрос на получение подборки с id = {}", compId);
        Compilation compilation = compilationRepository.findById(compId).orElse(null);
        if (compilation == null) {
            log.info("Подборки с id {} не найдено", compId);
            throw new RequestError(HttpStatus.NOT_FOUND,
                    "Подборки под id " + compId + " не найдено");
        }
        return CompilationMapper.toCompilationDto(compilation,
                eventService.getEventShortListWithSort(compilation.getEvents(), false));
    }

    @Override
    public Collection<CompilationDto> getCompilationWithParam(boolean pinned, Integer from, Integer size) {
        log.info("Запрос на получение подборки, где pinned - {}", pinned);
        List<CompilationDto> result = new ArrayList<>();
        from = from / size;
        Page<Compilation> compilations = compilationRepository
                .getCompilationsByPinned(pinned, PageRequest.of(from, size));
        compilations.forEach(compilation -> result.add(CompilationMapper.toCompilationDto(compilation,
                eventService.getEventShortListWithSort(compilation.getEvents(), false))));
        return result;
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilationRequest) {
        Compilation compilation = compilationRepository.findById(compId).orElse(null);
        if (compilation == null) {
            log.info("Подборки с id {} не найдено", compId);
            throw new RequestError(HttpStatus.NOT_FOUND,
                    "Подборки под id " + compId + " не найдено");
        }
        if (compilationRequest.getEvents().size() != 0) {
            log.info("Администратор изменил события в подборке");
            compilation.setEvents(eventService.getEventListByEventIds(compilationRequest.getEvents()));
        }
        if (compilationRequest.getTitle() != null) {
            log.info("Администратор изменил заголовок подборки");
            compilation.setTitle(compilationRequest.getTitle());
        }
        if (compilationRequest.getPinned() != null) {
            log.info("Администратор закрепил/открепил событие");
            compilation.setPinned(compilationRequest.getPinned());
        }
        Compilation compilationResult = compilationRepository.save(compilation);
        log.info("Изменения в подборке сохранены");
        return CompilationMapper.toCompilationDto(compilationResult,
                eventService.getEventShortListWithSort(compilationResult.getEvents(), false));
    }

    @Override
    public void deleteCompilation(Integer compId) {
        Compilation compilation = compilationRepository.findById(compId).orElse(null);
        if (compilation == null) {
            log.info("Подборки с id {} не найдено", compId);
            throw new RequestError(HttpStatus.NOT_FOUND,
                    "Подборки под id " + compId + " не найдено");
        }
        log.info("Удаление подборки {}", compilation);
        compilationRepository.deleteById(compId);
    }
}
