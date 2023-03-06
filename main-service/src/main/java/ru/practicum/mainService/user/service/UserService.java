package ru.practicum.mainService.user.service;

import ru.practicum.mainService.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long id);



}
