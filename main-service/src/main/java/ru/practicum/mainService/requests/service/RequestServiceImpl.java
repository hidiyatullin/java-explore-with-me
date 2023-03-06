package ru.practicum.mainService.requests.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exeption.IncorrectDataException;
import ru.practicum.mainService.exeption.NotFoundException;
import ru.practicum.mainService.requests.dto.ParticipationRequestDto;
import ru.practicum.mainService.requests.mapper.RequestMapper;
import ru.practicum.mainService.requests.model.ParticipationRequest;
import ru.practicum.mainService.requests.model.RequestEventStatus;
import ru.practicum.mainService.requests.repository.RequestRepository;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Setter
public class RequestServiceImpl implements RequestService{

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {
        if (requestRepository.findByEventIdAndRequesterId(eventId, userId) != null) {
            throw new IncorrectDataException("Запрос от пользователя с id уже существует " + userId);
        }
        User user = getUserById(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id " + eventId + " нет в списке"));
        if (event.getInitiator().getId().equals(userId)) {
            throw new IncorrectDataException("Пользователь с id " + userId + "не может опубликовать запрос");
        }
        int countRequestConfirmed = requestRepository.countParticipationRequestByEventIdAndStatus(eventId, RequestEventStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= countRequestConfirmed) {
            throw new IncorrectDataException("У события достигнут лимит запросов на участие");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new IncorrectDataException("Событие не имеет статус Опубликовано");
        }
        ParticipationRequest request = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .created(LocalDateTime.now())
                .status(RequestEventStatus.PENDING)
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestEventStatus.CONFIRMED);
        }
        return RequestMapper.toRequestEventDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getAllRequests(Long userId) {
        User user = getUserById(userId);
        List<ParticipationRequest> eventRequests = requestRepository.findAllByUser(user.getId());

        return eventRequests.stream()
                .map(RequestMapper::toRequestEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запроса с id " + requestId + " нет в списке"));
        User user = getUserById(userId);

        request.setStatus(RequestEventStatus.CANCELED);
        return RequestMapper.toRequestEventDto(requestRepository.save(request));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id " + userId + " нет в списке"));

    }
}
