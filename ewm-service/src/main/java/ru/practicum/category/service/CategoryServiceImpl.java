package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.error.RequestError;

import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto createNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Добавление новой категории {}", newCategoryDto);
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public Collection<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Запрошен список категорий");
        from = from / size;
        Collection<CategoryDto> result = new ArrayList<>();
        categoryRepository.getAllWithPagination(PageRequest.of(from, size)).forEach(category ->
                result.add(CategoryMapper.toCategoryDto(category)));
        return result;
    }

    @Override
    public CategoryDto getCategoryById(Integer catId) {
        Category category = checkContainCategory(catId);
        log.info("Запрошена категория {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public void deleteCategory(Integer catId) {
        checkContainCategory(catId);
        log.info("Удаление категории {}", catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto changeCategory(Integer catId, NewCategoryDto newCategoryDto) {
        Category category = checkContainCategory(catId);
        log.info("Изменение категории {} на категорию {}", catId, newCategoryDto);
        category.setName(newCategoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    private Category checkContainCategory(Integer catId) {
        Category category = categoryRepository.findById(catId).orElse(null);
        if (category == null) {
            throw new RequestError(HttpStatus.NOT_FOUND, "Категории под id " + catId + " не найдено");
        }
        return category;
    }
}
