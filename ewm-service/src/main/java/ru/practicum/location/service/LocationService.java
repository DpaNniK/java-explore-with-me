package ru.practicum.location.service;

import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;

public interface LocationService {

    Location saveLocation(LocationDto locationDto);

    void deleteLocation(Location location);
}
