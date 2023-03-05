package ru.practicum.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.dto.StatsOutputDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

//Посоветовали в пачке использовать нативные запросы, чтобы можно было получить нужную дтошку прямо в репозитории
//Удобно, чтобы не писать jdbc запросы, но может быть можно это как-то более красиво сделать
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "statistics", schema = "public")
@NamedNativeQuery(name = "FindStatsWithUriAndNotUniqueIp",
        query = "select a.name as app,  s.uri, count(s.ip) as hits from statistics s join apps a " +
                "on s.app_id = a.id " +
                "where s.timestamp > :start " +
                "and  s.timestamp < :end and (s.uri in :uris) " +
                "group by s.uri, a.name order by hits desc", resultSetMapping = "StatsOutputDtoModel")
@NamedNativeQuery(name = "FindStatsWithUriAndUniqueIp",
        query = "select a.name as app,  s.uri, count(distinct s.ip) as hits from statistics s join apps a " +
                "on s.app_id = a.id " +
                "where s.timestamp > :start " +
                "and  s.timestamp < :end and (s.uri in :uris) " +
                "group by s.uri, a.name order by hits desc", resultSetMapping = "StatsOutputDtoModel")
@NamedNativeQuery(name = "FindAllStatsWithNotUniqueIp",
        query = "select a.name as app,  s.uri, count(s.ip) as hits from statistics s join apps a " +
                "on s.app_id = a.id " +
                "where s.timestamp > :start " +
                "and  s.timestamp < :end " +
                "group by s.uri, a.name order by hits desc", resultSetMapping = "StatsOutputDtoModel")
@NamedNativeQuery(name = "FindAllStatsWithUniqueIp",
        query = "select a.name as app,  s.uri, count(distinct s.ip) as hits from statistics s join apps a " +
                "on s.app_id = a.id " +
                "where s.timestamp > :start " +
                "and  s.timestamp < :end " +
                "group by s.uri, a.name order by hits desc", resultSetMapping = "StatsOutputDtoModel")
@SqlResultSetMapping(name = "StatsOutputDtoModel", classes = {
        @ConstructorResult(
                columns = {
                        @ColumnResult(name = "app", type = String.class),
                        @ColumnResult(name = "uri", type = String.class),
                        @ColumnResult(name = "hits", type = Integer.class),
                },
                targetClass = StatsOutputDto.class
        )
})
public class StatisticModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "app_id")
    private App app;
    @Column(name = "uri")
    private String uri;
    @Column(name = "ip")
    private String ip;
    @DateTimeFormat
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StatisticModel that = (StatisticModel) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
