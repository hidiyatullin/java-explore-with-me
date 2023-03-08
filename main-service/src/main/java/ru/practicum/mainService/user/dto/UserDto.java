package ru.practicum.mainService.user.dto;

import lombok.*;
import ru.practicum.mainService.exeption.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @Positive(groups = Create.class)
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @Email(groups = Create.class)
    @NotBlank(groups = Create.class)
    private String email;
}
