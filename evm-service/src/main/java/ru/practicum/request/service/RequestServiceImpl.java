package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.error.RequestError;
import ru.practicum.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.state.RequestState;
import ru.practicum.state.State;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    public ParticipationRequestDto createNewRequestFromUser(Integer userId, Integer eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        checkCorrectlyRequest(user, event);
        Request request = new Request();
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestState.CONFIRMED);
        } else {
            request.setStatus(RequestState.PENDING);
        }
        request.setEvent(event);
        Request resultRequest = requestRepository.save(request);
        log.info("Создан новый запрос {} на участие в событие {} от пользователя {}",
                resultRequest, event, user);
        return RequestMapper.toParticipationRequestDto(resultRequest);
    }

    private void checkCorrectlyRequest(User user, Event event) {
        if (event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING)) {
            log.info("Участие в событии {} невозможно, статус события - {}", event, event.getState());
            throw new RequestError(HttpStatus.CONFLICT, "Событие имеет статус " + event.getState());
        }
        if (Objects.equals(user.getId(), event.getInitiator().getId())) {
            log.info("Невозможно создать запрос, пользователь {} " +
                    "является организатором события {}", user, event);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user + " " +
                    "является организатором ");
        }
        if (event.getParticipantLimit() <= getConfirmedRequest(event.getId())) {
            log.info("Невозможно добавить участника события. Достигнут лимит участников");
            throw new RequestError(HttpStatus.CONFLICT, "Достигнут лимит участников");
        }
        Request request = requestRepository.getRequestByRequesterAndEvent(user, event);
        if (request != null) {
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user +
                    "уже подавал заявку на событие " + event);
        }
    }

    @Override
    public Integer getConfirmedRequest(Integer eventId) {
        return requestRepository.getConfirmedRequest(eventId, RequestState.CONFIRMED);
    }

    @Override
    public Collection<ParticipationRequestDto> getRequestListForParticipationEvent(Integer userId,
                                                                                   Integer eventId) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        log.info("Получен запрос на получение пользователем {} список запросов " +
                "на участие в событии {}", user, event);
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.info("Невозможно просмотреть список заявок на участие в событии. " +
                    "Пользователь {} не является организатором события {}", user, event);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user +
                    " не является основателем события " + event);
        }
        Collection<Request> requests = requestRepository.getRequestsByEvent(event);
        return getParticipationRequestDtoList(requests);
    }

    @Override
    public Collection<ParticipationRequestDto> getUserRequestList(Integer userId) {
        User user = userService.getUserById(userId);
        log.info("Пользователь {} запросил список своих запросов на участие в событиях", user);
        Collection<Request> requests = requestRepository.getRequestsByRequester(user);
        return getParticipationRequestDtoList(requests);
    }

    @Override
    public ParticipationRequestDto cancelParticipationRequest(Integer userId, Integer requestId) {
        User user = userService.getUserById(userId);
        Request request = getRequestById(requestId);
        if (!Objects.equals(request.getRequester().getId(), userId)) {
            log.info("Пользователь {} не создавал заявку {}, " +
                    "невозможно изменить статус", user, request);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user + "не является " +
                    "создателем запроса " + request + " на участие в событии");
        }
        log.info("Пользователь {} закрыл свою заявку {} на участие в событии", user, request);
        request.setStatus(RequestState.CANCELED);
        Request resultReq = requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(resultReq);
    }

    @Override
    public EventRequestStatusUpdateResult changeStateUserRequests(Integer userId, Integer eventId,
                                                                  EventRequestStatusUpdateRequest requests) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);
        if (!Objects.equals(userId, event.getInitiator().getId())) {
            log.info("Пользователь {} не является основателем события {}", user, event);
            throw new RequestError(HttpStatus.CONFLICT, "Пользователь " + user + "не является " +
                    "основателем события " + event);
        }
        log.info("Запрос от пользователя {} на изменения статуса заявок в событии по id - {}",
                user, requests.getRequestIds());
        List<Request> resultRequests = new ArrayList<>(requestRepository.
                getRequestByRequesterIds(requests.getRequestIds()));

        resultRequests.forEach(request -> {
            if (request.getStatus() != RequestState.PENDING) {
                log.info("Пользователь неверно передал список заявок. " +
                        "Заявки имеют статус, отличающийся от {}", RequestState.PENDING);
                throw new RequestError(HttpStatus.CONFLICT, "Невозможно изменить статус заявки," +
                        " ее статус " + request.getStatus());
            }
        });
        Integer confirmedReqNow = requestRepository.getConfirmedRequest(eventId, RequestState.CONFIRMED);
        if (Objects.equals(confirmedReqNow, event.getParticipantLimit())) {
            throw new RequestError(HttpStatus.CONFLICT, "Достигнут лимит по заявкам");
        }
        if (requests.getStatus().equals(RequestState.REJECTED)) {
            log.info("Все заявки отклонены");
            resultRequests.forEach(request -> {
                request.setStatus(RequestState.REJECTED);
                requestRepository.save(request);
            });
            return EventMapper.toEventRequestStatusUpdateResult(new ArrayList<>(), resultRequests);
        }
        return changeStateRequestWithParticipantLimit(confirmedReqNow, resultRequests, event);
    }

    private EventRequestStatusUpdateResult changeStateRequestWithParticipantLimit(Integer confirmedReqNow,
                                                                                  List<Request> resultRequests,
                                                                                  Event event) {
        Request request;
        Collection<Request> allRequestForEvent = requestRepository.getRequestsByEvent(event);
        Collection<Request> acceptRequest = new ArrayList<>();
        Collection<Request> rejectRequest = new ArrayList<>();

        for (int i = 0; i < resultRequests.size(); i++) {
            if (confirmedReqNow <= event.getParticipantLimit()) {
                request = resultRequests.get(0);
                log.info("Заявка на участие {} одобрена создателем события", request);
                allRequestForEvent.remove(request);
                request.setStatus(RequestState.CONFIRMED);
                Request resultReq = requestRepository.save(request);
                acceptRequest.add(resultReq);
                confirmedReqNow++;
            } else {
                log.info("Достигнут лимит по заявкам на участие, все оставшиеся заявки {} " +
                        "автоматически отклонены", allRequestForEvent);
                allRequestForEvent.forEach(req -> {
                    req.setStatus(RequestState.REJECTED);
                    Request resultReq = requestRepository.save(req);
                    rejectRequest.add(resultReq);
                });
                break;
            }
        }
        return EventMapper.toEventRequestStatusUpdateResult(acceptRequest, rejectRequest);
    }

    private Collection<ParticipationRequestDto> getParticipationRequestDtoList(Collection<Request> requests) {
        Collection<ParticipationRequestDto> resultListDto = new ArrayList<>();
        requests.forEach(request -> resultListDto.add(RequestMapper.toParticipationRequestDto(request)));
        return resultListDto;
    }

    private Request getRequestById(Integer id) {
        Request request = requestRepository.findById(id).orElse(null);
        if (request == null) {
            log.info("Ошибка при получении запроса с id {}, запрос не найден", id);
            throw new RequestError(HttpStatus.NOT_FOUND, "Запрос с id " + id + " не найден");
        }
        log.info("Найден запрос под id {}", id);
        return request;
    }
}
