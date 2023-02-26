package controller;

import dto.EndPointStatsClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.ViewStats;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import service.StatsService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
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
