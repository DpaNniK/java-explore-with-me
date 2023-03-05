package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsOutputDto;
import ru.practicum.exception.RequestError;
import ru.practicum.mapper.AppMapper;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.model.App;
import ru.practicum.model.StatisticModel;
import ru.practicum.repository.AppsRepository;
import ru.practicum.repository.StatsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServerServiceImpl implements StatsServerService {

    private final StatsRepository statsRepository;
    private final AppsRepository appsRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void saveStatistics(StatsDto statsDto) {
        App app = getOrCreateApp(statsDto);
        log.info("Сохранение в статистику запроса " + statsDto.getUri() +
                " от пользователя с ip - " + statsDto.getIp());
        StatisticModel statisticModel = StatsMapper.toStaticModel(statsDto, app);
        statsRepository.save(statisticModel);
    }

    @Override
    public Collection<StatsOutputDto> getStats(String start, String end, List<String> uris, boolean unique) {

        LocalDateTime startFormat = LocalDateTime.parse(start, dateTimeFormatter);
        LocalDateTime endFormat = LocalDateTime.parse(end, dateTimeFormatter);
        checkCorrectlyTimePeriod(startFormat, endFormat);
        Collection<StatsOutputDto> resultCollection;

        if (uris.stream().anyMatch(uri -> uri.equals("/events"))) {
            if (unique) {
                log.info("Получен запрос статистики по всем событиям с уникальным IP");
                resultCollection = statsRepository.getAllStatsWithUniqIp(startFormat, endFormat);
            } else {
                log.info("Получен запрос статистики по всем событиям с без уникальности IP");
                resultCollection = statsRepository.getAllStatsWithNotUniqIp(startFormat, endFormat);
            }
        } else {
            if (unique) {
                log.info("Получен запрос статистики по URI - {} с уникальным IP", uris);
                resultCollection = statsRepository.getStatsWithUniqIp(startFormat, endFormat, uris);
            } else {
                log.info("Получен запрос статистики по URI - {} без уникальности IP", uris);
                resultCollection = statsRepository.getStatsWithNotUniqIp(startFormat, endFormat, uris);
            }
        }
        return resultCollection;
    }

    private App getOrCreateApp(StatsDto statsDto) {
        return appsRepository.getAppByName(statsDto.getApp())
                .orElseGet(() -> appsRepository.save(AppMapper.toApp(statsDto)));
    }

    private void checkCorrectlyTimePeriod(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start) || start.isAfter(LocalDateTime.now())) {
            log.info("Неверный запрос при попытки выгрузки статистики за период с " + start + " по " + end);
            throw new RequestError(HttpStatus.BAD_REQUEST, "Неверно указан временной период для отображения" +
                    "статистики");
        }
    }
}
