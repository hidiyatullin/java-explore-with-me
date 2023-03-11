package ru.practicum.mainService.comments.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long authorId;
    private Long eventId;

    @NotBlank
    private String text;
    private LocalDateTime createdOn;
}
