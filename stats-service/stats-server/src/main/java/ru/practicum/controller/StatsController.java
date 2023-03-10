package ru.practicum.controller;

import org.springframework.http.HttpStatus;
import ru.practicum.dto.EndPointStatsClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.ViewStats;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndPointStatsClientDto save(@Valid @RequestBody EndPointStatsClientDto endPointStatsClientDto) {
        return statsService.save(endPointStatsClientDto);
    }

    @GetMapping("/stats")
    public List<ViewStats> getViewStats(@RequestParam String start, @RequestParam String end,
                                        @RequestParam(required = false) List<String> uris,
                                        @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        return statsService.getViewStats(start, end, uris, unique);
    }
}
