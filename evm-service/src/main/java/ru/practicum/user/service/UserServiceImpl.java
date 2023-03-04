package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.error.RequestError;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        log.info("Добавление нового пользователя {} в базу данных", newUserRequest);
        User newUser = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(newUser);
    }

    @Override
    public Collection<UserDto> getAllUser(List<Integer> ids) {
        log.info("Запрос на получение пользователей из списка: {}", ids);
        Collection<User> users = userRepository.getUsersByIds(ids);
        Collection<UserDto> usersDto = new ArrayList<>();
        users.forEach(user -> usersDto.add(UserMapper.toUserDto(user)));
        return usersDto;
    }

    @Override
    public Collection<UserDto> getAllUserWithPagination(List<Integer> ids, Integer from, Integer size) {
        log.info("Запрос на получение пользователей из списка : {} с использованием пагинации", ids);
        from = from / size;
        Page<User> users = userRepository.getUsersByIdsWithPagination(ids, PageRequest.of(from, size));
        Collection<UserDto> usersDto = new ArrayList<>();
        users.forEach(user -> usersDto.add(UserMapper.toUserDto(user)));
        return usersDto;
    }

    @Override
    public User getUserById(Integer userId) {
        log.info("Запрос на получение пользователя под id {}", userId);
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Неверно указан ID пользователя {} для удаления", userId);
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id " + userId + " не найден");
        }
        return user;
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.info("Неверно указан ID пользователя {} для удаления", userId);
            throw new RequestError(HttpStatus.NOT_FOUND, "Пользователь с id " + userId + " не найден");
        }
        log.info("Пользователь {} удален", user);
        userRepository.deleteById(userId);
    }
}
