package ru.practicum.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.dto.EventShortDto;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    Integer id;
    Collection<EventShortDto> events;
    boolean pinned;
    String title;
}
