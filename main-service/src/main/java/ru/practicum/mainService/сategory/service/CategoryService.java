package ru.practicum.mainService.сategory.service;

import ru.practicum.mainService.сategory.dto.CategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(Long catId, CategoryDto categoryDto);

    void delete(Long catId);

    CategoryDto getById(Long catId);

    List<CategoryDto> getAll(Integer from, Integer size);
}
