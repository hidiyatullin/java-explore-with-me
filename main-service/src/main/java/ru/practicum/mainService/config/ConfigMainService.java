package ru.practicum.mainService.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.client.HitClient;

@Configuration
public class ConfigMainService {

    @Value("http://localhost:9090")
    private String serverUrl;

    @Bean
    public HitClient hitClient() {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        return new HitClient(serverUrl, builder);
    }
}
