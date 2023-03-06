package ru.practicum.mainService.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventUpdateRequestDto;
import ru.practicum.mainService.event.dto.NewEventDto;
import ru.practicum.mainService.event.service.EventService;

import javax.validation.constraints.*;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

    private EventService eventService;

    @PutMapping("{eventId}")
    public EventFullDto update(@PathVariable @NotNull Long eventId,
                               @RequestBody NewEventDto newEventDto) {
        return eventService.update(newEventDto, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventByAdmin(@PathVariable @NotNull Long eventId,
                                           @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateEventByAdmin(eventId, eventUpdateRequestDto);
    }

    @GetMapping
    public List<EventFullDto> getEventsByParams(@RequestParam(required = false) List<Long> users,
                                                   @RequestParam(defaultValue = "PUBLISHED", required = false) List<String> states,
                                                   @RequestParam(required = false) List<Long> categories,
                                                   @RequestParam(required = false) String rangeStart,
                                                   @RequestParam(required = false) String rangeEnd,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsByParams(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
