package ru.practicum.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.service.EventService;
import ru.practicum.user.dto.UpdateEventUserRequest;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping("/{userId}/events")
    public Collection<EventShortDto> findAllFounderEvents(@PathVariable Integer userId,
                                                          @PositiveOrZero
                                                          @RequestParam(value = "from", defaultValue = "0",
                                                                  required = false) Integer from,
                                                          @Positive
                                                          @RequestParam(value = "size", defaultValue = "10",
                                                                  required = false)
                                                          Integer size) {
        return eventService.findAllFounderEvents(userId, from, size);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/events")
    public EventFullDto createNewEventByUser(@PathVariable Integer userId,
                                             @RequestBody @Valid NewEventDto eventDto) {
        return eventService.createNewEvent(userId, eventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findFounderEvent(@PathVariable Integer userId,
                                         @PathVariable Integer eventId) {
        return eventService.findFounderEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto changeEventByUser(@PathVariable Integer userId,
                                          @PathVariable Integer eventId,
                                          @RequestBody @Valid UpdateEventUserRequest eventUserRequest) {
        return eventService.changeEventByUser(userId, eventId, eventUserRequest);
    }

    //Эндопинт для подписки на пользователя
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{userId}/subscribe/{initiatorId}")
    public void subscribeToUser(@PathVariable Integer userId,
                                @PathVariable Integer initiatorId) {
        userService.subscribeToUser(userId, initiatorId);
    }

    //Эндопинт для получения всех подписчиков пользователя
    @GetMapping("/{userId}/subscribers")
    public Collection<UserShortDto> getSubscribersForUser(@PathVariable Integer userId) {
        return userService.getSubscribersForUser(userId);
    }

    //Эндопит, чтобы отписаться от пользователя
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}/unsubscribe/{initiatorId}")
    public void unSubscribeToUser(@PathVariable Integer userId,
                                @PathVariable Integer initiatorId) {
        userService.unSubscribeToUser(userId, initiatorId);
    }
}
