package ru.practicum.service;

import ru.practicum.dto.EndPointStatsClientDto;
import ru.practicum.model.ViewStats;

import java.util.List;

public interface StatsService {

    EndPointStatsClientDto save(EndPointStatsClientDto endPointStatsClientDto);

    List<ViewStats> getViewStats(String startDate, String endDate, List<String> uris, Boolean unique);
}
