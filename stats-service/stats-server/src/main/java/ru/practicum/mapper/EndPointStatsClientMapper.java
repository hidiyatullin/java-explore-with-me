package ru.practicum.mapper;

import ru.practicum.dto.EndPointStatsClientDto;
import ru.practicum.model.EndPointStatsClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndPointStatsClientMapper {

    public static EndPointStatsClientDto toEndPointStatsClientDto(EndPointStatsClient endPointStatsClient) {
        return EndPointStatsClientDto.builder()
                .id(endPointStatsClient.getId())
                .app(endPointStatsClient.getApp())
                .uri(endPointStatsClient.getUri())
                .ip(endPointStatsClient.getIp())
                .timestamp(endPointStatsClient.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    public static EndPointStatsClient toEndPointStatsClient(EndPointStatsClientDto endPointStatsClientDto) {
        return EndPointStatsClient.builder()
                .app(endPointStatsClientDto.getApp())
                .uri(endPointStatsClientDto.getUri())
                .ip(endPointStatsClientDto.getIp())
                .timestamp(LocalDateTime.parse(endPointStatsClientDto.getTimestamp(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }
}
