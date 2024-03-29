package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.event.dto.EventWithRequestNum;
import ru.practicum.state.State;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer>, EventRepositoryCustom {

    @Query("select distinct e from Event e where e.initiator.id = :userId")
    Page<Event> findAllFounderEvents(@Param("userId") Integer userId, Pageable pageable);

    @Query(nativeQuery = true, name = "FindRequestForEventWithStateConfirmed")
    Collection<EventWithRequestNum> getConfirmedRequestMap(@Param("events") Collection<Integer> events,
                                                           @Param("status") String status);

    Collection<Event> getEventsByIdIn(Collection<Integer> ids);

    @Query("select distinct e from Event e " +
            "where e.initiator.id in :initiatorIds " +
            "and e.state = :state " +
            "and e.eventDate >= :nowDate " +
            "order by e.eventDate")
    Collection<Event> getActualEventsForSubscriber(@Param("initiatorIds") Collection<Integer> initiatorIds,
                                                   @Param("state") State state,
                                                   @Param("nowDate") LocalDateTime nowDate);
}
