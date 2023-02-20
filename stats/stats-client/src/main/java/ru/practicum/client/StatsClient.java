package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsOutputDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class StatsClient {
    private final String local = "http://localhost:9090";
    private final RestTemplate restTemplate = new RestTemplate();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void saveStatistics(StatsDto statsDto) {
        log.info("Запрос на сохранение {}", statsDto);
        restTemplate.postForLocation(local + "/hit", statsDto);
    }

    public List<StatsOutputDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        LocalDateTime startFormat = LocalDateTime.parse(start.toString(), dateTimeFormatter);
        LocalDateTime endFormat = LocalDateTime.parse(end.toString(), dateTimeFormatter);
        log.info("Запрос на получение статистики с {} по {}, уникальность IP - {}", startFormat, endFormat, unique);
        ResponseEntity<StatsOutputDto[]> list = restTemplate.getForEntity(local + "/hit?start=" + startFormat +
                        "&end=" + endFormat + "&unique=" + unique + "uris=" + uris,
                StatsOutputDto[].class);
        return Arrays.asList(Objects.requireNonNull(list.getBody()));
    }
}
