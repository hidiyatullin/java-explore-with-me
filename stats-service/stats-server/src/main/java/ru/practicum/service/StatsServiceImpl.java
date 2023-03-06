package ru.practicum.service;

import ru.practicum.dto.EndPointStatsClientDto;
import ru.practicum.mapper.EndPointStatsClientMapper;
import ru.practicum.repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.model.EndPointStatsClient;
import ru.practicum.model.ViewStats;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EndPointStatsClientDto save(EndPointStatsClientDto endPointStatsClientDto) {
        EndPointStatsClient endPointStatsClient = EndPointStatsClientMapper.toEndPointStatsClient(endPointStatsClientDto);
        endPointStatsClient.setTimestamp(LocalDateTime.now());
        return EndPointStatsClientMapper.toEndPointStatsClientDto(statsRepository.save(endPointStatsClient));
    }

    @Override
    public List<ViewStats> getViewStats(String startDate, String endDate, List<String> uris, Boolean unique) {
//        LocalDateTime startStat;
//        LocalDateTime endStat;
//        if (uris.isEmpty()) {
//            throw new ValidationException("Uris для подсчета статистики не переданы");
//        }
//        startStat = LocalDateTime.parse(URLDecoder.decode(startDate, StandardCharsets.UTF_8), formatter);
//        endStat = LocalDateTime.parse(URLDecoder.decode(endDate, StandardCharsets.UTF_8), formatter);
//
//        if (unique) {
//            return statsRepository.findAllUnique(startStat, endStat, uris, unique);
//        } else {
//            return statsRepository.findAll(startStat, endStat, uris);
//        }
        LocalDateTime startOfTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endOfTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (unique) {
            return statsRepository.findAllUnique(startOfTime, endOfTime, uris, unique);
        } else {
            return statsRepository.findAll(startOfTime, endOfTime, uris);
        }
    }
}
