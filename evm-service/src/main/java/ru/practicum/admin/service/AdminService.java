package ru.practicum.admin.service;

import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.admin.dto.UpdateEventAdminRequest;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.state.State;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface AdminService {

    CategoryDto createNewCategory(NewCategoryDto newCategoryDto);

    CategoryDto changeCategory(Integer catId, NewCategoryDto newCategoryDto);

    UserDto createUser(NewUserRequest newUserRequest);

    Collection<UserDto> getAllUsers(List<Integer> ids, Integer from, Integer size);

    EventFullDto changeEvent(Integer eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Collection<EventFullDto> findEvents(List<Integer> users, List<State> states, List<Integer> categories,
                                        String rangeStart, String rangeEnd, Integer from, Integer size);

    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    CompilationDto updateCompilation(Integer compId, UpdateCompilationRequest compilationRequest);

    void deleteUser(Integer userId);

    void deleteCategory(Integer catId);

    void deleteCompilation(Integer compId);

}
