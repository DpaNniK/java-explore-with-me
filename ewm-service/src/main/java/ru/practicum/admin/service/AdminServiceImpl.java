package ru.practicum.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.admin.dto.UpdateEventAdminRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.error.RequestError;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.location.model.Location;
import ru.practicum.location.service.LocationService;
import ru.practicum.state.ActionState;
import ru.practicum.state.State;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {
    private final LocationService locationService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;
    private final CompilationRepository compilationRepository;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public CategoryDto createNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Создание новой категории {} администратором", newCategoryDto);
        return categoryService.createNewCategory(newCategoryDto);
    }

    @Override
    public CategoryDto changeCategory(Integer catId, NewCategoryDto newCategoryDto) {
        log.info("Обновление категории {} администратором", catId);
        return categoryService.changeCategory(catId, newCategoryDto);
    }

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        log.info("Создание пользователя {} администратором", newUserRequest);
        return userService.addNewUser(newUserRequest);
    }

    @Override
    public Collection<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size) {
        log.info("Получение администратором списка пользователей из списка {}", ids);
        if (from == null && size == null) {
            return userService.getAllUser(ids);
        } else {
            return userService.getAllUserWithPagination(ids, from, size);
        }
    }

    @Override
    public EventFullDto changeEvent(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event oldEvent = eventService.getEventById(eventId);
        if (oldEvent.getState() != State.PENDING) {
            log.info("Невозможно изменить статус события. Текущий статус {}", oldEvent.getState());
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно изменить статус события");
        }
        if (oldEvent.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            log.info("Дата начала события должна быть не ранее чем за час от даты публикации");
            throw new RequestError(HttpStatus.CONFLICT, "Дата начала события должна быть " +
                    "не ранее чем за час от даты публикации");
        }
        if (updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now())) {
            log.info("Невозможно изменить событие, новая дата события уже наступила");
            throw new RequestError(HttpStatus.CONFLICT, "Невозможно изменить событие, " +
                    "новая дата события уже наступила");
        }
        Event resultEvent = setNewParameters(oldEvent, updateEventAdminRequest);
        resultEvent.setId(eventId);
        log.info("Событие {} изменено на {}", oldEvent, resultEvent);

        return eventService.saveChangeEventForAdmin(resultEvent);
    }

    @Override
    public Collection<EventFullDto> findEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                               String rangeStart, String rangeEnd, Integer from, Integer size) {
        LocalDateTime startFormat = null;
        LocalDateTime endFormat = null;

        if (rangeStart != null) {
            startFormat = LocalDateTime.parse(rangeStart, dateTimeFormatter);
        }
        if (rangeEnd != null) {
            endFormat = LocalDateTime.parse(rangeEnd, dateTimeFormatter);
        }
        log.info("Запрос на получение списка событий с заданными параметрами" +
                " администратором ");

        return eventService.findEventsWithParameters(users, states, categories,
                startFormat, endFormat, from, size);
    }

    @Override
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        log.info("Создание новой подборки {}", newCompilationDto);
        Collection<Event> events = eventService.getEventListByEventIds(newCompilationDto.getEvents());
        Compilation compilation = CompilationMapper.toCompilation(newCompilationDto, events);
        Compilation compilationResult = compilationRepository.save(compilation);

        return CompilationMapper.toCompilationDto(compilationResult,
                eventService.getEventShortListWithSort(compilationResult.getEvents(), false));
    }

    @Override
    public CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilationRequest) {
        log.info("Изменение подборки с id {} администратором", compId);
        return compilationService.updateCompilation(compId, compilationRequest);
    }

    @Override
    public void deleteUser(Integer userId) {
        log.info("Запрос на удаление администратором пользователя {}", userId);
        userService.deleteUser(userId);
    }

    @Override
    public void deleteCategory(Integer catId) {
        log.info("Запрос на удалении администратором категории {}", catId);
        categoryService.deleteCategory(catId);
    }

    @Override
    public void deleteCompilation(Integer compId) {
        log.info("Запрос на удаление администратором подборки {}", compId);
        compilationService.deleteCompilation(compId);
    }

    private Event setNewParameters(Event event, UpdateEventAdminRequest updateEvent) {
        if (updateEvent.getAnnotation() == null && updateEvent.getDescription() == null &&
                updateEvent.getParticipantLimit() == null && updateEvent.getLocation() == null &&
                updateEvent.getTitle() == null) {
            if (updateEvent.getStateAction().equals(ActionState.PUBLISH_EVENT)) {
                event.setState(State.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            } else {
                event.setState(State.CANCELED);
            }
            return event;
        }

        return updateFullEventParamsByAdmin(event, updateEvent);
    }

    private Event updateFullEventParamsByAdmin(Event event, UpdateEventAdminRequest updateEvent) {
        if (event.getCategory() != null && !Objects.equals(event.getCategory().getId(),
                updateEvent.getCategory())) {
            Category newCategory = CategoryMapper.toCategory(categoryService
                    .getCategoryById(updateEvent.getCategory()));
            event.setCategory(newCategory);
        }
        if (event.getLocation() != null && !Objects.equals(event.getLocation().getLat(),
                updateEvent.getLocation().getLat()) ||
                !Objects.equals(event.getLocation().getLon(), updateEvent.getLocation().getLon())) {
            locationService.deleteLocation(event.getLocation());
            Location newLocation = locationService.saveLocation(updateEvent.getLocation());
            event.setLocation(newLocation);
        }
        NewEventDto eventDto = EventMapper.toNewEventDto(updateEvent);
        Event resultEvent = EventMapper.toEvent(event.getInitiator(), event.getCategory(),
                event.getLocation(), eventDto);
        if (updateEvent.getStateAction().equals(ActionState.PUBLISH_EVENT)) {
            resultEvent.setState(State.PUBLISHED);
            resultEvent.setPublishedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        } else {
            resultEvent.setState(State.CANCELED);
        }
        return resultEvent;
    }
}
