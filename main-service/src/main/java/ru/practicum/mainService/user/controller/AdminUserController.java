package ru.practicum.mainService.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("admin/users")
public class AdminUserController {

    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(name = "ids", required = false) List<Long> ids,
                                @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
