package ru.practicum.service;

import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsOutputDto;

import java.util.Collection;
import java.util.List;

public interface StatsServerService {

    void saveStatistics(StatsDto statsDto);

    Collection<StatsOutputDto> getStats(String start, String end, List<String> uris, boolean unique);
}
