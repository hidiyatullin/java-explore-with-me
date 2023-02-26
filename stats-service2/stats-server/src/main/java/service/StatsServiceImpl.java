package service;

import dto.EndPointStatsClientDto;
import mapper.EndPointStatsClientMapper;
import repository.StatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.EndPointStatsClient;
import model.ViewStats;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public EndPointStatsClientDto save(EndPointStatsClientDto endPointStatsClientDto) {
        EndPointStatsClient endPointStatsClient = EndPointStatsClientMapper.toEndPointStatsClient(endPointStatsClientDto);
        endPointStatsClient.setTimestamp(LocalDateTime.now());
        return EndPointStatsClientMapper.toEndPointStatsClientDto(statsRepository.save(endPointStatsClient));
    }

    @Override
    public List<ViewStats> getViewStats(String startDate, String endDate, List<String> uris, Boolean unique) {
        LocalDateTime startTime = LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endTime = LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if(unique) {
            return statsRepository.findAllUnique(startTime, endTime, uris, unique);
        } else {
            return statsRepository.findAll(startTime, endTime, uris);
        }
    }
}
