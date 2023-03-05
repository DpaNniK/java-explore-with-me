package ru.practicum.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;
import ru.practicum.location.model.Location;
import ru.practicum.location.repository.LocationRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public Location saveLocation(LocationDto locationDto) {
        log.info("Сохранение локации {}", locationDto);
        return locationRepository.save(LocationMapper.toLocation(locationDto));
    }

    @Override
    public void deleteLocation(Location location) {
        log.info("Удаление локации {}", location);
        locationRepository.delete(location);
    }

}
