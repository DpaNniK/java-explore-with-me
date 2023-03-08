package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto addNewUser(NewUserRequest newUserRequest);

    Collection<UserDto> getAllUser(List<Integer> ids);

    Collection<UserDto> getAllUserWithPagination(List<Integer> ids, Integer from, Integer size);

    Collection<UserShortDto> getSubscribersForUser(Integer userId);

    User getUserById(Integer userId);

    void subscribeToUser(Integer userId, Integer initiatorId);

    void unSubscribeToUser(Integer userId, Integer initiatorId);

    void deleteUser(Integer userId);
}
