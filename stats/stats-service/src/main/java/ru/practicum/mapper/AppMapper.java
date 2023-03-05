package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.dto.StatsDto;
import ru.practicum.model.App;

@UtilityClass
public class AppMapper {

    public static App toApp(StatsDto statsDto) {
        App app = new App();
        app.setName(statsDto.getApp());
        return app;
    }
}
