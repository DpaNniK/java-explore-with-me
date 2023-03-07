package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto compilationDto, Collection<Event> events) {
        Compilation compilation = new Compilation();
        compilation.setEvents(events);
        compilation.setTitle(compilationDto.getTitle());
        compilation.setPinned(compilationDto.isPinned());
        return compilation;
    }

    public static CompilationDto toCompilationDto(Compilation compilation, Collection<EventShortDto> eventShorts) {
        CompilationDto compilationDto = new CompilationDto();
        Collection<EventShortDto> events = eventShorts.stream().sorted(Comparator.comparing(EventShortDto::getViews)
                .reversed()).collect(Collectors.toList());
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.isPinned());
        compilationDto.setTitle(compilation.getTitle());
        compilationDto.setEvents(events);
        return compilationDto;
    }
}
