package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatsOutputDto;
import ru.practicum.model.StatisticModel;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<StatisticModel, Integer> {

    @Query(nativeQuery = true, name = "FindStatsWithUriAndNotUniqueIp")
    Collection<StatsOutputDto> getStatsWithNotUniqIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(nativeQuery = true, name = "FindStatsWithUriAndUniqueIp")
    Collection<StatsOutputDto> getStatsWithUniqIp(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(nativeQuery = true, name = "FindAllStatsWithNotUniqueIp")
    Collection<StatsOutputDto> getAllStatsWithNotUniqIp(LocalDateTime start, LocalDateTime end);

    @Query(nativeQuery = true, name = "FindAllStatsWithUniqueIp")
    Collection<StatsOutputDto> getAllStatsWithUniqIp(LocalDateTime start, LocalDateTime end);
}
