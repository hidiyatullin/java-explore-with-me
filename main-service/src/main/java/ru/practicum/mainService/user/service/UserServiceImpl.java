package ru.practicum.mainService.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.exeption.ConflictException;
import ru.practicum.mainService.user.dto.UserDto;
import ru.practicum.mainService.user.mapper.UserMapper;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        try {
            userRepository.save(user);
            return UserMapper.toDto(user);
        } catch (DataIntegrityViolationException sql) {
            throw new ConflictException("Запрос приводит к нарушению целостности данных");
        }
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return userRepository.findAllByIdIn(ids, pageRequest)
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
