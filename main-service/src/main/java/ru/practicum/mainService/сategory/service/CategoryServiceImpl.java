package ru.practicum.mainService.сategory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.exeption.IncorrectDataException;
import ru.practicum.mainService.exeption.NotFoundException;
import ru.practicum.mainService.сategory.dto.CategoryDto;
import ru.practicum.mainService.сategory.mapper.CategoryMapper;
import ru.practicum.mainService.сategory.model.Category;
import ru.practicum.mainService.сategory.repository.CategoryRepository;
import ru.practicum.mainService.event.repository.EventRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    @Override
    @Transactional
    public CategoryDto create(CategoryDto categoryDto) {
        validateCategory(categoryDto);
        Category category = CategoryMapper.toCategory(categoryDto);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        validateCategory(categoryDto);
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + categoryDto.getId() + " не существует"));
        category.setName(categoryDto.getName());
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не существует"));
        if (eventRepository.findByCategoryId(catId).isPresent()) {
            throw new IncorrectDataException("Существуют события, связанные с категорией");
        }
        categoryRepository.delete(category);
    }

    @Override
    public CategoryDto getById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Категория с id " + catId + " не существует"));
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAll(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    private void validateCategory(CategoryDto categoryDto) {
        String name = categoryDto.getName();
        if (categoryRepository.findByName(name) != null) {
            throw new IncorrectDataException("Имя " + categoryDto.getName() + " уже существует");
        }
    }
}
