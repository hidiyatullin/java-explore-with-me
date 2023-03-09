package ru.practicum.mainService.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.requests.dto.ParticipationRequestDto;
import ru.practicum.mainService.requests.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(@PathVariable @NotNull Long userId,
                                          @RequestParam Long eventId) {
        return requestService.create(userId, eventId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequests(@PathVariable Long userId) {
        return requestService.getAllRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable @NotNull Long userId,
                                                 @PathVariable @NotNull Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
