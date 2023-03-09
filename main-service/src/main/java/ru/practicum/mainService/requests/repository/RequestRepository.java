package ru.practicum.mainService.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.mainService.requests.model.ParticipationRequest;
import ru.practicum.mainService.requests.model.RequestEventStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query(nativeQuery = true,
            value = "select * from requests as r " +
                    "where  r.requester_id = ?1 ")
    List<ParticipationRequest> findAllByUser(Long userId);

    Integer countParticipationRequestByEventIdAndStatus(Long eventId, RequestEventStatus status);

    @Query(nativeQuery = true,
            value = "select * from requests as r " +
                    "where  r.event_id = ?1 " +
                    "and r.requester_id = ?2 ")
    ParticipationRequest findByEventIdAndRequesterId(Long eventId, Long userId);

    @Query(nativeQuery = true,
            value = "select * from requests as r " +
                    "where  r.event_id = ?1 ")
    List<ParticipationRequest> findAllByEventId(Long eventId);
}
