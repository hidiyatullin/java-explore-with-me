package ru.practicum.mainService.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.event.dto.EventFullDto;
import ru.practicum.mainService.event.dto.EventShortDto;
import ru.practicum.mainService.event.dto.EventUpdateRequestDto;
import ru.practicum.mainService.event.dto.NewEventDto;
import ru.practicum.mainService.event.service.EventService;
import ru.practicum.mainService.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainService.requests.dto.ParticipationRequestDto;
import ru.practicum.mainService.requests.dto.ParticipationRequestStatusUpdate;

import javax.validation.Valid;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto eventNewDto) {
        return eventService.create(userId, eventNewDto);
    }

    @GetMapping
    public List<EventShortDto> getAllEventsByUser(@PathVariable Long userId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(name = "size", defaultValue = "10") int size) {
        return eventService.getAllEventsByUser(userId, from, size);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestEventByUser(@PathVariable Long userId,
                                                               @PathVariable Long eventId) {
        return eventService.getRequestEventByUser(userId, eventId);
    }

    @GetMapping("{eventId}")
    public EventFullDto getEventByIdByCreator(@PathVariable Long userId,
                                              @PathVariable Long eventId) {
        return eventService.getEventByIdByCreator(userId, eventId);
    }

    @PatchMapping("{eventId}")
    public EventFullDto updateByCreator(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody EventUpdateRequestDto eventUpdateRequestDto) {
        return eventService.updateByCreator(userId, eventId, eventUpdateRequestDto);

    }

    @PatchMapping("/{eventId}/requests/{reqId}/confirm")
    public ParticipationRequestDto confirmRequestForEvent(@PathVariable Long userId,
                                                          @PathVariable Long eventId,
                                                          @PathVariable Long reqId) {
        return eventService.confirmRequestForEvent(userId, eventId, reqId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestForEvent(@PathVariable Long userId,
                                                                @PathVariable Long eventId,
                                                                @RequestBody ParticipationRequestStatusUpdate participationRequestDto) {
        return eventService.updateRequestStatusForEvent(userId, eventId, participationRequestDto);
    }
}
