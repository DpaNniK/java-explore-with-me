package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.App;
import ru.practicum.model.StatisticModel;

@UtilityClass
public class StatsMapper {

    public static StatisticModel toStaticModel(StatsDto statsDto, App app) {
        StatisticModel statisticModel = new StatisticModel();
        statisticModel.setApp(app);
        statisticModel.setUri(statsDto.getUri());
        statisticModel.setIp(statsDto.getIp());
        statisticModel.setTimestamp(statsDto.getTimestamp());

        return statisticModel;
    }
}
