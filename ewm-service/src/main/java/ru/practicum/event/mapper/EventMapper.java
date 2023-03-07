package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.admin.dto.UpdateEventAdminRequest;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.model.Location;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@UtilityClass
public class EventMapper {

    public static Event toEvent(User user, Category category, Location location, NewEventDto newEventDto) {
        Event event = new Event();
        event.setCreatedOn(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        event.setAnnotation(newEventDto.getAnnotation());
        event.setEventDate(newEventDto.getEventDate());
        event.setDescription(newEventDto.getDescription());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setPaid(newEventDto.isPaid());
        event.setInitiator(user);
        event.setLocation(location);
        event.setCategory(category);
        return event;
    }

    public static EventFullDto toEventFullDto(Event event, Integer confirmedRequests, Integer views) {
        EventFullDto eventFullDto = new EventFullDto();
        eventFullDto.setConfirmedRequests(Objects
                .requireNonNullElse(confirmedRequests, 0));
        eventFullDto.setViews(Objects.requireNonNullElse(views, 0));
        eventFullDto.setId(event.getId());
        eventFullDto.setDescription(event.getDescription());
        eventFullDto.setAnnotation(event.getAnnotation());
        eventFullDto.setState(event.getState());
        eventFullDto.setCategory(event.getCategory());
        eventFullDto.setPublishedOn(event.getPublishedOn());
        eventFullDto.setInitiator(event.getInitiator());
        eventFullDto.setLocation(event.getLocation());
        eventFullDto.setPaid(event.isPaid());
        eventFullDto.setTitle(event.getTitle());
        eventFullDto.setCreatedOn(event.getCreatedOn());
        eventFullDto.setEventDate(event.getEventDate());
        eventFullDto.setRequestModeration(event.isRequestModeration());
        eventFullDto.setParticipantLimit(event.getParticipantLimit());
        return eventFullDto;
    }

    public static NewEventDto toNewEventDto(UpdateEventAdminRequest updateEvent) {
        NewEventDto newEventDto = new NewEventDto();
        newEventDto.setAnnotation(updateEvent.getAnnotation());
        newEventDto.setDescription(updateEvent.getDescription());
        newEventDto.setPaid(updateEvent.isPaid());
        newEventDto.setLocation(updateEvent.getLocation());
        newEventDto.setCategory(updateEvent.getCategory());
        newEventDto.setParticipantLimit(updateEvent.getParticipantLimit());
        newEventDto.setRequestModeration(updateEvent.isRequestModeration());
        newEventDto.setTitle(updateEvent.getTitle());
        newEventDto.setEventDate(updateEvent.getEventDate());
        return newEventDto;
    }

    public static EventShortDto toEventShortDto(Event event, Integer confirmedRequest, Integer views) {
        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(event.getId());
        eventShortDto.setAnnotation(event.getAnnotation());
        eventShortDto.setEventDate(event.getEventDate());
        eventShortDto.setPaid(event.isPaid());
        eventShortDto.setConfirmedRequests(confirmedRequest);
        eventShortDto.setViews(views);
        eventShortDto.setTitle(event.getTitle());
        eventShortDto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        eventShortDto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        return eventShortDto;
    }


    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(Collection<Request> acceptRequest,
                                                                                  Collection<Request> rejectRequest) {
        Collection<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        Collection<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        acceptRequest.forEach(request ->
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(request)));
        rejectRequest.forEach(request ->
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(request)));
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }
}
