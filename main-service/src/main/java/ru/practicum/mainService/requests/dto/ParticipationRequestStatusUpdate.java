package ru.practicum.mainService.requests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainService.requests.model.RequestEventStatus;

import java.util.List;

@Getter
@Setter
@Builder
public class ParticipationRequestStatusUpdate {
    private List<Long> requestIds;
    private RequestEventStatus status;
}
