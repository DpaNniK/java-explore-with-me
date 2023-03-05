package ru.practicum.user.service;

import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    UserDto addNewUser(NewUserRequest newUserRequest);

    Collection<UserDto> getAllUser(List<Integer> ids);

    Collection<UserDto> getAllUserWithPagination(List<Integer> ids, Integer from, Integer size);

    User getUserById(Integer userId);

    void deleteUser(Integer userId);
}
