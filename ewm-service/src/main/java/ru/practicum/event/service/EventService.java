package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.state.SortState;
import ru.practicum.state.State;
import ru.practicum.user.dto.UpdateEventUserRequest;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {
    EventFullDto createNewEvent(Integer userId, NewEventDto eventDto);

    Event getEventById(Integer eventId);

    Collection<Event> getEventListByEventIds(Collection<Integer> ids);

    EventFullDto saveChangeEventForAdmin(Event event);

    Collection<EventShortDto> getEventShortListWithSort(Collection<Event> events, Boolean onlyAvailable);

    EventFullDto getFullEventById(Integer id, String ip);

    EventFullDto changeEventByUser(Integer userId, Integer eventId,
                                   UpdateEventUserRequest eventUserRequest);

    EventFullDto findFounderEvent(Integer userId, Integer eventId);

    Collection<EventFullDto> findEventsWithParameters(List<Integer> users, List<State> states,
                                                      List<Integer> categories, LocalDateTime rangeStart,
                                                      LocalDateTime rangeEnd, Integer from, Integer size);

    Collection<EventShortDto> findAllFounderEvents(Integer userId, Integer from, Integer size);

    Collection<EventShortDto> findPublicEventsWithParameters(String text, Collection<Integer> categories,
                                                             Boolean paid, String rangeStart, String rangeEnd,
                                                             Boolean onlyAvailable, SortState sort, Integer from,
                                                             Integer size, String ip);

    Collection<EventShortDto> getActualEventsForSubscriber(Integer userId);
}
