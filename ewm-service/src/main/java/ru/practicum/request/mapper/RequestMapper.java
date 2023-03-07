package ru.practicum.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        requestDto.setId(request.getId());
        requestDto.setRequester(request.getRequester().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setEvent(request.getEvent().getId());
        requestDto.setStatus(request.getStatus());
        return requestDto;
    }
}
