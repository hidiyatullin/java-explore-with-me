package ru.practicum.mainService.compilation.mapper;

import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.model.Compilation;
import ru.practicum.mainService.event.model.Event;

import java.util.stream.Collectors;

import static ru.practicum.mainService.event.mapper.Formatter.FORMATTER;

public class CompilationMapper {
    public static CompilationDto toCompilDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents()
                        .stream()
                        .map(CompilationMapper::toEventShortDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    private static CompilationDto.EventShortDto toEventShortDto(Event event) {
        return CompilationDto.EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CompilationDto.EventShortDto.CategoryDto.builder()
                        .id(event.getCategory().getId())
                        .name(event.getCategory().getName())
                        .build())
                .eventDate(event.getEventDate().format(FORMATTER))
                .initiator(CompilationDto.EventShortDto.UserShortDto.builder()
                        .id(event.getInitiator().getId())
                        .name(event.getInitiator().getName())
                        .build())
                .paid(event.getIsPaid())
                .title(event.getTitle())
                .build();
    }
}
