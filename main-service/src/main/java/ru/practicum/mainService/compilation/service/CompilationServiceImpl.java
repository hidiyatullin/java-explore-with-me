package ru.practicum.mainService.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.compilation.dto.CompilationDto;
import ru.practicum.mainService.compilation.dto.NewCompilationDto;
import ru.practicum.mainService.compilation.mapper.CompilationMapper;
import ru.practicum.mainService.compilation.model.Compilation;
import ru.practicum.mainService.compilation.repository.CompilationRepository;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exeption.NotFoundException;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getAllCompilations(Boolean pinned, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        if (Objects.isNull(pinned)) {
            compilationRepository.findAll(pageRequest);
        }
        return compilationRepository.findAllByPinned(pinned, pageRequest)
                .stream()
                .map(CompilationMapper::toCompilDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>(eventRepository.findAllById(newCompilationDto.getEvents()));
        Compilation newCompilation = Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
        compilationRepository.save(newCompilation);
        return CompilationMapper.toCompilDto(newCompilation);
    }

    @Override
    @Transactional
    public void deleteCompilationById(Long compId) {
        getCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto pinnedOutCompilation(Long id) {
        Compilation compilation = getCompilationById(id);
        compilation.setPinned(false);
        compilationRepository.save(compilation);
        return CompilationMapper.toCompilDto(compilation);
    }

    @Override
    public CompilationDto getCompilationDtoById(Long id) {
        return CompilationMapper.toCompilDto(getCompilationById(id));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = getCompilationById(compId);
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        if (newCompilationDto.getTitle() != null) {
            compilation.setTitle(newCompilationDto.getTitle());
        }
        if (newCompilationDto.getEvents() != null) {
            compilation.setEvents(newCompilationDto.getEvents().stream()
                    .map(this::getEventById).collect(Collectors.toList()));
        }
        return CompilationMapper.toCompilDto(compilationRepository.save(compilation));
    }

    private Compilation getCompilationById(Long id) {
        return compilationRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет компиляции с id " + id));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
    }
}
