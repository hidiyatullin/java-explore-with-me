package ru.practicum.mainService.category.service;

import ru.practicum.mainService.category.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(Long catId, CategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Integer from, Integer size);
}
