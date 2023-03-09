package ru.practicum.mainService.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@Builder
public class EventUpdateRequestDto {
    private Long eventId;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Positive
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private NewEventDto.LocationDto location;

    @NotNull
    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    @NotNull
    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    private StateAction stateAction;

    @Getter
    @Setter
    @Builder
    public static class LocationDto {
        private Double lat;
        private Double lon;
    }
}
