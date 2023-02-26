package mapper;

import dto.EndPointStatsClientDto;
import model.EndPointStatsClient;

public class EndPointStatsClientMapper {

    public static EndPointStatsClientDto toEndPointStatsClientDto(EndPointStatsClient endPointStatsClient) {
        return EndPointStatsClientDto.builder()
                .id(endPointStatsClient.getId())
                .app(endPointStatsClient.getApp())
                .uri(endPointStatsClient.getUri())
                .ip(endPointStatsClient.getIp())
                .timestamp(endPointStatsClient.getTimestamp())
                .build();
    }

    public static EndPointStatsClient toEndPointStatsClient(EndPointStatsClientDto endPointStatsClientDto) {
        return EndPointStatsClient.builder()
                .app(endPointStatsClientDto.getApp())
                .uri(endPointStatsClientDto.getUri())
                .ip(endPointStatsClientDto.getIp())
                .timestamp(endPointStatsClientDto.getTimestamp())
                .build();
    }
}
