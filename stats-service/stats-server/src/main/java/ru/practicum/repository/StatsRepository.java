package ru.practicum.repository;

import ru.practicum.model.EndPointStatsClient;
import ru.practicum.model.ViewStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndPointStatsClient, Long> {

    @Query("SELECT new ru.practicum.model.ViewStats(e.app, e.uri, COUNT (e.ip)) " +
            "from EndPointStatsClient e WHERE e.timestamp> ?1 AND e.timestamp< ?2 and e.uri in ?3 GROUP BY e.app, e.uri ORDER BY COUNT (e.ip) DESC")
    List<ViewStats> findAll(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.model.ViewStats(e.app, e.uri, COUNT (DISTINCT e.ip)) from " +
            "EndPointStatsClient e WHERE e.timestamp> ?1 AND e.timestamp< ?2 and e.uri in ?3 GROUP BY e.app, e.uri ORDER BY COUNT (e.ip) DESC")
    List<ViewStats> findAllUnique(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
