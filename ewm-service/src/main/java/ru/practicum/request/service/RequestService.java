package ru.practicum.request.service;

import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.Collection;

public interface RequestService {

    ParticipationRequestDto createNewRequestFromUser(Integer userId, Integer eventId);

    Integer getConfirmedRequest(Integer eventId);

    Collection<ParticipationRequestDto> getRequestListForParticipationEvent(Integer userId, Integer eventId);

    Collection<ParticipationRequestDto> getUserRequestList(Integer userId);

    ParticipationRequestDto cancelParticipationRequest(Integer userId, Integer requestId);

    EventRequestStatusUpdateResult changeStateUserRequests(Integer userId, Integer eventId,
                                                           EventRequestStatusUpdateRequest requests);
}
