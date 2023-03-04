package ru.practicum.admin;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.dto.UpdateCompilationRequest;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.admin.dto.UpdateEventAdminRequest;
import ru.practicum.admin.service.AdminService;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.state.State;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/categories")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto createNewCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return adminService.createNewCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Integer catId) {
        adminService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto changeCategory(@PathVariable Integer catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        return adminService.changeCategory(catId, newCategoryDto);
    }

    @GetMapping("/events")
    public Collection<EventFullDto> getAllEvents(@RequestParam(value = "users", required = false)
                                                 List<Integer> users,
                                                 @RequestParam(value = "states", required = false)
                                                 List<State> states,
                                                 @RequestParam(value = "categories", required = false)
                                                 List<Integer> categories,
                                                 @RequestParam(value = "rangeStart", required = false)
                                                 String rangeStart,
                                                 @RequestParam(value = "rangeEnd", required = false)
                                                 String rangeEnd,
                                                 @PositiveOrZero
                                                 @RequestParam(value = "from", defaultValue = "0", required = false)
                                                 Integer from,
                                                 @Positive
                                                 @RequestParam(value = "size", defaultValue = "10", required = false)
                                                 Integer size) {
        return adminService.findEvents(users, states, categories, rangeStart,
                rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto changeEvent(@PathVariable Integer eventId,
                                    @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {

        return adminService.changeEvent(eventId, updateEventAdminRequest);
    }

    @GetMapping("/users")
    public Collection<UserDto> getAllUsers(@RequestParam(value = "ids") List<Integer> ids,
                                           @PositiveOrZero
                                           @RequestParam(value = "from", required = false, defaultValue = "0")
                                           Integer from,
                                           @Positive
                                           @RequestParam(value = "size", required = false, defaultValue = "10")
                                           Integer size) {
        return adminService.getAllUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto createNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return adminService.createUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer userId) {
        adminService.deleteUser(userId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return adminService.createNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compId) {
        adminService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable Integer compId,
                                            @RequestBody UpdateCompilationRequest compilationRequest) {
        return adminService.updateCompilation(compId, compilationRequest);
    }
}
