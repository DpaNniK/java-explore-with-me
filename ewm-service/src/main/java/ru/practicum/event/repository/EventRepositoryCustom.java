package ru.practicum.event.repository;

import ru.practicum.event.model.Event;
import ru.practicum.state.SortState;
import ru.practicum.state.State;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepositoryCustom {

    List<Event> findEventsForAdmin(List<Integer> users,
                                   List<State> states,
                                   List<Integer> categories,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Integer from,
                                   Integer size);

    List<Event> findPublicEventsWithParameters(String text, Collection<Integer> categories,
                                               Boolean paid, String rangeStart, String rangeEnd,
                                               SortState sort, Integer from,
                                               Integer size);
}
