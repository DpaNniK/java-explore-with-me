package ru.practicum.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsOutputDto;
import ru.practicum.service.StatsServerService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
public class StatsServerController {
    private final StatsServerService statsServerService;

    @PostMapping(path = "/hit")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void saveStatistics(@RequestBody @Valid StatsDto statsDto) {
        statsServerService.saveStatistics(statsDto);
    }

    @GetMapping(path = "/stats")
    public Collection<StatsOutputDto> getStats(@RequestParam(value = "start") String start,
                                               @RequestParam(value = "end") String end,
                                               @RequestParam(value = "uris") List<String> uris,
                                               @RequestParam(value = "unique", defaultValue = "false") boolean unique) {
        return statsServerService.getStats(start, end, uris, unique);
    }
}
