package ru.practicum.mainService.requests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainService.requests.model.RequestEventStatus;

@Getter
@Setter
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private String created;
    private RequestEventStatus status;
}
