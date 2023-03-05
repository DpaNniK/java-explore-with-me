package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    CategoryDto createNewCategory(NewCategoryDto newCategoryDto);

    Collection<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Integer catId);

    void deleteCategory(Integer catId);

    CategoryDto changeCategory(Integer catId, NewCategoryDto newCategoryDto);
}
