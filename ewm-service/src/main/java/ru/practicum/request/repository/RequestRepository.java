package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;
import ru.practicum.request.model.Request;
import ru.practicum.state.RequestState;

import ru.practicum.user.model.User;

import java.util.Collection;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    @Query("select count (r.id) from Request r where r.event.id = ?1 and r.status = ?2")
    Integer getConfirmedRequest(Integer eventId, RequestState state);

    Request getRequestByRequesterAndEvent(User requester, Event event);

    Collection<Request> getRequestsByEvent(Event event);

    Collection<Request> getRequestsByRequester(User requester);

    @Query("select distinct r from Request r where r.id in :ids")
    Collection<Request> getRequestByRequesterIds(Collection<Integer> ids);
}
