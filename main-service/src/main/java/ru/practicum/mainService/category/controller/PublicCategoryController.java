package ru.practicum.mainService.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getById(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAll(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                    Integer from,
                                    @Positive @RequestParam(name = "size", defaultValue = "10")
                                    Integer size) {
        return categoryService.getAll(from, size);
    }
}
