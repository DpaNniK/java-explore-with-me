package ru.practicum.category;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    Collection<CategoryDto> getCategories(@PositiveOrZero
                                          @RequestParam(value = "from", defaultValue = "0", required = false)
                                          Integer from,
                                          @Positive
                                          @RequestParam(value = "size", defaultValue = "10", required = false)
                                          Integer size) {
        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto getCategoryById(@PathVariable Integer catId) {
        return categoryService.getCategoryById(catId);
    }
}
