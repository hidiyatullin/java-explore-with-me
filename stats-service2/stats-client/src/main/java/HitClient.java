import dto.EndPointStatsClientDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class HitClient extends BaseClient {
    @Autowired
    public HitClient(@Value("http://stats-server:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> createHit(EndPointStatsClientDto endPointStatsClientDto) {
        return post("/hit", endPointStatsClientDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        Map<String, Object> parameters = Map.of(
                "start", start,
                "end", end,
                "uris", uris.get(0),
                "unique", unique.toString()
        );
        return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }

}