package ru.practicum.mainService.requests.mapper;

import ru.practicum.mainService.requests.dto.ParticipationRequestDto;
import ru.practicum.mainService.requests.model.ParticipationRequest;

import static ru.practicum.mainService.event.mapper.Formatter.FORMATTER;

public class RequestMapper {

    public static ParticipationRequestDto toRequestEventDto(ParticipationRequest participationRequest) {
        return ParticipationRequestDto.builder()
                .id(participationRequest.getId())
                .requester(participationRequest.getRequester().getId())
                .event(participationRequest.getEvent().getId())
                .status(participationRequest.getStatus())
                .created(participationRequest.getCreated().format(FORMATTER))
                .build();
    }
}
