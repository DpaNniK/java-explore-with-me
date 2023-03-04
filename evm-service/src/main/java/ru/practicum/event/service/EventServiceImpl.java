package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.StatsDto;
import ru.practicum.dto.StatsOutputDto;
import ru.practicum.error.RequestError;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventWithRequestNum;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.state.NewEventState;
import ru.practicum.state.RequestState;
import ru.practicum.state.SortState;
import ru.practicum.state.State;
import ru.practicum.user.dto.UpdateEventUserRequest;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final EventRepository eventRepository;

    private final StatsClient statsClient;
    private final String app = "evm-service";

    private final DateTimeFormatter dateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto createNewEvent(Integer userId, NewEventDto eventDto) {
        User user = userService.getUserById(userId);
        if (eventDto.getEventDate() != null && eventDto.getEventDate()
                .isBefore(LocalDateTime.now())) {
            log.info("Невозможно создать событие. Указанная дата {} уже прошла",
                    eventDto.getEventDate());
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно создать событие. " +
                    "Указанная дата " + eventDto.getEventDate() + " уже прошла");
        }
        Category category = CategoryMapper
                .toCategory(categoryService.getCategoryById(eventDto.getCategory()));
        Location location = locationService.saveLocation(eventDto.getLocation());
        Event event = EventMapper.toEvent(user, category, location, eventDto);
        log.info("Создание нового события {} пользователем {}", event, user);
        event.setState(State.PENDING);
        Event resultEven = eventRepository.save(event);
        return EventMapper.toEventFullDto(resultEven, 0, 0);
    }

    @Override
    public Event getEventById(Integer eventId) {
        log.info("Запрошено событие под id {}", eventId);
        return checkContainEvent(eventId);
    }

    @Override
    public Collection<Event> getEventListByEventIds(Collection<Integer> ids) {
        log.info("Запрошен список событий, имеющих id из списка {}", ids);
        return eventRepository.getEventsByIdIn(ids);
    }

    @Override
    public EventFullDto saveChangeEventForAdmin(Event event) {
        log.info("Сохранение изменений в событии {} администратором", event);
        event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Event resultEven = eventRepository.save(event);
        return EventMapper.toEventFullDto(resultEven, 0, 0);
    }

    @Override
    public EventFullDto getFullEventById(Integer id, String ip) {
        Event event = getEventById(id);
        statsClient.saveStatistics(new StatsDto(app, "/event/" + id, ip, LocalDateTime.now()));
        log.info("Получен запрос на просмотр события {}", event);
        if (!event.getState().equals(State.PUBLISHED)) {
            log.info("Невозможно получить событие под id {}, статус события {}", id, event.getState());
            throw new RequestError(HttpStatus.CONFLICT, "Событие не является опубликованным");
        }
        Collection<Integer> eventIds = new ArrayList<>();
        eventIds.add(event.getId());

        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(eventIds);
        Map<Integer, Integer> viewsMap = getViewsMap(List.of(event), List.of("/event/" + event.getId()));
        return EventMapper.toEventFullDto(event, confirmedRequestMap.getOrDefault(id, 0),
                viewsMap.getOrDefault(event.getId(), 0));
    }

    @Override
    public EventFullDto changeEventByUser(Integer userId, Integer eventId, UpdateEventUserRequest eventUserRequest) {
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            log.info("Пользователь {} не может изменить событие {}, " +
                    "т.к. он не является основателем", user, eventId);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user +
                    " не является основателем события " + eventId);
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            log.info("дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента ");
            throw new RequestError(HttpStatus.CONFLICT, "Дата и время на которое намечено " +
                    "событие не может быть раньше, чем через два часа от текущего момента");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            log.info("Невозможно изменить событие, оно является опубликованным {}", event.getState());
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно изменить событие," +
                    " оно уже опубликовано");
        }
        if (eventUserRequest.getEventDate() != null && eventUserRequest.getEventDate()
                .isBefore(LocalDateTime.now())) {
            log.info("Невозможно создать событие. Указанная дата {} уже прошла",
                    eventUserRequest.getEventDate());
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно создать событие. " +
                    "Указанная дата " + eventUserRequest.getEventDate() + " уже прошла");
        }
        return setNewParamsForEvent(event, eventUserRequest);
    }

    @Override
    public EventFullDto findFounderEvent(Integer userId, Integer eventId) {
        log.info("Запрос от пользователя с id {} на просмотр своего события с id {}",
                userId, eventId);
        User user = userService.getUserById(userId);
        Event event = getEventById(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            log.info("Пользователь {} не является создателем события {}", user, event);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user +
                    " не является создателем события " + event);
        }
        Collection<Integer> eventIds = new ArrayList<>();
        eventIds.add(eventId);
        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(eventIds);
        Map<Integer, Integer> viewsMap = getViewsMap(List.of(event), List.of("/event/" + eventId));
        return EventMapper.toEventFullDto(event, confirmedRequestMap.getOrDefault(eventId, 0),
                viewsMap.getOrDefault(eventId, 0));
    }

    @Override
    public Collection<EventFullDto> findEventsWithParameters(List<Integer> users, List<State> states,
                                                             List<Integer> categories, LocalDateTime rangeStart,
                                                             LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Запрос от администратора на поиск событий с заданными параметрами");
        from = from / size;
        Collection<EventFullDto> resultEventFullDto = new ArrayList<>();
        Collection<Integer> eventIds = new ArrayList<>();
        List<String> uris = new ArrayList<>();
        Collection<Event> result = eventRepository
                .findEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        result.forEach(event -> {
            eventIds.add(event.getId());
            uris.add("/event/" + event.getId());
        });

        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(eventIds);
        Map<Integer, Integer> viewsMap = getViewsMap(result, uris);
        result.forEach(event -> resultEventFullDto.add(EventMapper.toEventFullDto(event,
                confirmedRequestMap.getOrDefault(event.getId(), 0),
                viewsMap.getOrDefault(event.getId(), 0))));
        return resultEventFullDto;
    }

    @Override
    public Collection<EventShortDto> findAllFounderEvents(Integer userId, Integer from, Integer size) {
        User user = userService.getUserById(userId);
        from = from / size;
        log.info("Пользователь {} запросил список добавленных им событий", user);
        Collection<EventShortDto> eventsShort = new ArrayList<>();
        Collection<Integer> eventIds = new ArrayList<>();
        List<String> uris = new ArrayList<>();

        Page<Event> userEvents = eventRepository.findAllFounderEvents(userId, PageRequest.of(from, size));
        userEvents.forEach(event -> {
            eventIds.add(event.getId());
            uris.add("/event/" + event.getId());
        });

        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(eventIds);
        Map<Integer, Integer> viewsMap = getViewsMap(userEvents.toList(), uris);

        userEvents.forEach(event -> eventsShort.add(EventMapper
                .toEventShortDto(event, confirmedRequestMap.getOrDefault(event.getId(), 0),
                        viewsMap.getOrDefault(event.getId(), 0))));
        return eventsShort.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<EventShortDto> findPublicEventsWithParameters(String text, Collection<Integer> categories,
                                                                    Boolean paid, String rangeStart, String rangeEnd,
                                                                    Boolean onlyAvailable, SortState sort, Integer from,
                                                                    Integer size, String ip) {
        log.info("Поиск всех опубликованных событий по заданным параметрам");
        statsClient.saveStatistics(new StatsDto(app, "/events", ip, LocalDateTime.now()));
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now().format(dateTimeFormatter);
        }
        Collection<Event> events = eventRepository.findPublicEventsWithParameters(text, categories, paid, rangeStart,
                rangeEnd, sort, from, size);
        Collection<EventShortDto> eventsShortDto = getEventShortListWithSort(events, onlyAvailable);

        if (sort != null && sort.equals(SortState.VIEWS)) {
            log.info("Список событий возвращен с сортировкой по просмотрам");
            return eventsShortDto.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        log.info("Список событий возвращен с сортировкой по дате");
        return eventsShortDto;
    }


    private EventFullDto setNewParamsForEvent(Event event, UpdateEventUserRequest eventUserRequest) {
        Map<Integer, Integer> viewsMap = getViewsMap(List.of(event), List.of("/event/" + event.getId()));
        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(List.of(event.getId()));
        log.info("Изменение события с id {} основателем", event.getId());
        if (eventUserRequest.getStateAction().equals(NewEventState.CANCEL_REVIEW)) {
            event.setState(State.CANCELED);
            Event resultEvent = eventRepository.save(event);
            return EventMapper.toEventFullDto(resultEvent,
                    confirmedRequestMap.getOrDefault(event.getId(), 0),
                    viewsMap.getOrDefault(event.getId(), 0));
        } else {
            event.setState(State.PENDING);
            log.info("Состояние события пользователя изменено на {}", State.PENDING);
        }
        if (eventUserRequest.getRequestModeration() != null) {
            log.info("Пользователь изменил состояние модерации события");
            event.setRequestModeration(eventUserRequest.getRequestModeration());
        }
        if (eventUserRequest.getDescription() != null) {
            log.info("Пользователь изменил описание события");
            event.setDescription(eventUserRequest.getDescription());
        }
        if (eventUserRequest.getEventDate() != null) {
            log.info("Пользователь изменил дату начала события");
            event.setEventDate(eventUserRequest.getEventDate());
        }
        if (eventUserRequest.getPaid() != null) {
            log.info("Пользователь изменил значение платности");
            event.setPaid(eventUserRequest.getPaid());
        }
        if (eventUserRequest.getAnnotation() != null) {
            log.info("Пользователь изменил аннотацию события");
            event.setAnnotation(eventUserRequest.getAnnotation());
        }
        if (eventUserRequest.getTitle() != null) {
            log.info("Пользователь изменил заголовок события");
            event.setTitle(eventUserRequest.getTitle());
        }
        if (eventUserRequest.getParticipantLimit() != null) {
            log.info("Пользователь установил новый лимит участников");
            event.setParticipantLimit(eventUserRequest.getParticipantLimit());
        }
        if (eventUserRequest.getCategory() != null) {
            log.info("Пользователь изменил категорию события");
            Category newCat = CategoryMapper.toCategory(categoryService
                    .getCategoryById(eventUserRequest.getCategory()));
            event.setCategory(newCat);
        }
        if (eventUserRequest.getLocation() != null &&
                !Objects.equals(event.getLocation().getLon(), eventUserRequest.getLocation().getLon()) &&
                !Objects.equals(event.getLocation().getLat(), eventUserRequest.getLocation().getLat())) {
            locationService.deleteLocation(event.getLocation());
            Location newLoc = locationService.saveLocation(eventUserRequest.getLocation());
            log.info("Пользователь изменил локацию события");
            event.setLocation(newLoc);
        }

        Event eventResult = eventRepository.save(event);
        return EventMapper.toEventFullDto(eventResult,
                confirmedRequestMap.getOrDefault(event.getId(), 0),
                viewsMap.getOrDefault(event.getId(), 0));
    }

    private Event checkContainEvent(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            log.info("Запрашиваемого события под id {} не найдено", eventId);
            throw new RequestError(HttpStatus.NOT_FOUND, "События под id " + eventId + " не найдено");
        }
        return event;
    }

    @Override
    public Collection<EventShortDto> getEventShortListWithSort(Collection<Event> events, Boolean onlyAvailable) {
        List<String> uris = new ArrayList<>();
        Collection<Integer> eventIds = new ArrayList<>();
        Collection<EventShortDto> eventShortList = new ArrayList<>();

        events.forEach(event -> {
            uris.add("/event/" + event.getId());
            eventIds.add(event.getId());
        });

        Map<Integer, Integer> viewsMap = getViewsMap(events, uris);
        Map<Integer, Integer> confirmedRequestMap = getConfirmedRequestMap(eventIds);

        if (onlyAvailable) {
            events.forEach(event -> {
                if (event.getParticipantLimit() > confirmedRequestMap.get(event.getId())) {
                    eventShortList.add(EventMapper.toEventShortDto(event,
                            confirmedRequestMap.getOrDefault(event.getId(), 0),
                            viewsMap.getOrDefault(event.getId(), 0)));
                }
            });
            return eventShortList;
        }
        events.forEach(event -> eventShortList.add(EventMapper.toEventShortDto(event,
                confirmedRequestMap.getOrDefault(event.getId(), 0),
                viewsMap.getOrDefault(event.getId(), 0))));
        return eventShortList;
    }

    private Map<Integer, Integer> getConfirmedRequestMap(Collection<Integer> eventIds) {
        Collection<EventWithRequestNum> resultConfirmedReq = eventRepository
                .getConfirmedRequestMap(eventIds, RequestState.CONFIRMED.toString());

        return resultConfirmedReq.stream().collect(Collectors.toMap(EventWithRequestNum::getEventId,
                EventWithRequestNum::getConfirmedRequestSize));
    }

    private Map<Integer, Integer> getViewsMap(Collection<Event> events, List<String> uris) {
        LocalDateTime startTime = getStartTimeForStatistic(events).minusMinutes(1);
        Map<Integer, Integer> resultViewsMap = new HashMap<>();
        Collection<StatsOutputDto> stats = statsClient.getStats(startTime, LocalDateTime.now().plusMinutes(1)
                        .truncatedTo(ChronoUnit.SECONDS),
                uris, false);
        stats.forEach(statsOutputDto -> resultViewsMap.put(
                Integer.parseInt(Arrays.stream(statsOutputDto.getUri().split("/"))
                        .collect(Collectors.toList()).get(2)),
                statsOutputDto.getHits()));
        return resultViewsMap;
    }

    private LocalDateTime getStartTimeForStatistic(Collection<Event> events) {
        List<LocalDateTime> timePublishedEvent = new ArrayList<>();
        List<LocalDateTime> finalTimePublishedEvent;

        events.forEach(event -> {
            if (event.getPublishedOn() != null) {
                timePublishedEvent.add(event.getPublishedOn());
            }
        });
        if (timePublishedEvent.size() == 0) {
            return LocalDateTime.now().minusMinutes(1).truncatedTo(ChronoUnit.SECONDS);
        }
        finalTimePublishedEvent = timePublishedEvent.stream().sorted(Comparator
                .comparing(LocalDateTime::getDayOfYear).reversed()).collect(Collectors.toList());
        return finalTimePublishedEvent.get(0);
    }
}
