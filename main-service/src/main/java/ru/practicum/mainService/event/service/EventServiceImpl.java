package ru.practicum.mainService.event.service;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.HitClient;
import ru.practicum.mainService.event.dto.*;
import ru.practicum.mainService.event.mapper.EventMapper;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.model.Location;
import ru.practicum.mainService.event.model.State;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exeption.ConflictException;
import ru.practicum.mainService.exeption.NotFoundException;
import ru.practicum.mainService.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.mainService.requests.dto.ParticipationRequestDto;
import ru.practicum.mainService.requests.dto.ParticipationRequestStatusUpdate;
import ru.practicum.mainService.requests.mapper.RequestMapper;
import ru.practicum.mainService.requests.model.ParticipationRequest;
import ru.practicum.mainService.requests.model.RequestEventStatus;
import ru.practicum.mainService.requests.repository.RequestRepository;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;
import ru.practicum.mainService.category.model.Category;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.mainService.event.mapper.Formatter.FORMATTER;
import static ru.practicum.mainService.requests.model.RequestEventStatus.CONFIRMED;
import static ru.practicum.mainService.requests.model.RequestEventStatus.REJECTED;

@Service
@Setter
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final HitClient hitClient;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto eventNewDto) {
        User user = findUserById(userId);
        Event event = EventMapper.toEvent(eventNewDto);
        if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
            throw new ConflictException("Время события не может быть раньше, чем через два часа");
        }
        event.setInitiator(user);
        return EventMapper.eventFullDto(eventRepository.save(event), 0);
    }

    @Override
    public List<EventShortDto> getAllEventsByUser(Long userId, int from, int size) {
        User user = userRepository.findById(userId).orElseThrow();
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiatorId(userId, pageRequest)
                .stream()
                .map(event -> EventMapper.eventShortDto(event,
                        Objects.requireNonNull(getCountConfirmedRequests(event.getId())).orElse(0)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto update(NewEventDto newEventDto, Long eventId) {
        Event event = findEventById(eventId);  // событие в базе, которое меняем
        checkBeforeSaveNewEvent(newEventDto, event);
        return EventMapper.eventFullDto(eventRepository.save(event),
                getCountConfirmedRequests(eventId).orElse(0));
    }

    @Override
    public EventFullDto getEventByIdByCreator(Long userId, Long eventId) {
        return EventMapper.eventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId),
                getCountConfirmedRequests(eventId).orElse(0));
    }

    @Override
    public List<EventShortDto> getEventsWithFilter(String text, List<Long> categoriesId, Boolean paid,
                                                   String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                                   String sort, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        String state = State.PUBLISHED.name();
        List<EventShortDto> events = eventRepository.findEvents(text, categoriesId, paid, state,
                        pageRequest)
                .stream()
                .filter(event -> rangeStart != null ?
                        event.getEventDate().isAfter(LocalDateTime.parse(rangeStart, FORMATTER)) :
                        event.getEventDate().isAfter(LocalDateTime.now())
                                && rangeEnd != null ? event.getEventDate().isBefore(LocalDateTime.parse(rangeEnd,
                                FORMATTER)) :
                                event.getEventDate().isBefore(LocalDateTime.MAX))
                .map(event -> EventMapper.eventShortDto(event, getCountConfirmedRequests(event.getId()).orElse(0)))//
                .collect(Collectors.toList());
        if (Boolean.TRUE.equals(onlyAvailable)) {
            events = events.stream().filter(shortEventDto ->
                    shortEventDto.getConfirmedRequests() < eventRepository.findById(shortEventDto.getId())
                            .get().getParticipantLimit() || eventRepository.findById(shortEventDto.getId())
                            .get().getParticipantLimit() == 0
            ).collect(Collectors.toList());
        }
        if (sort != null) {
            switch (sort) {
                case "EVENT_DATE":
                    events = events
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getEventDate))
                            .collect(Collectors.toList());
                    break;
                case "VIEWS":
                    events = events
                            .stream()
                            .sorted(Comparator.comparing(EventShortDto::getViews))
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new ConflictException("Вариант сортировки: по дате события или по количеству просмотров");
            }
        }
        return events
                .stream()
                .peek(shortEventDto -> getViews(shortEventDto.getId()))
                .peek(shortEventDto -> shortEventDto.setViews(getViews(shortEventDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventById(Long id) {
        Event event = findEventById(id);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Cобытие должно быть опубликовано");
        }
        getViews(id);
        EventFullDto fullDto = EventMapper.eventFullDto(event, getCountConfirmedRequests(id).orElse(0));
        fullDto.setViews(getViews(id));
        return fullDto;
    }

    @Override
    public EventFullDto updateByCreator(Long userId, Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = findEventById(eventId);

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Событие может изменить только текущий пользователь");
        }
        if (!event.getState().equals(State.PUBLISHED)) {

            NewEventDto newEventDto = EventMapper.newEventDto(eventUpdateRequestDto);
            checkBeforeSaveNewEvent(newEventDto, event);

            if (LocalDateTime.now().plusHours(2).isAfter(event.getEventDate())) {
                throw new ConflictException("Время события не может быть раньше, чем через два часа");
            }
            switch (eventUpdateRequestDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
            }
            return EventMapper.eventFullDto(eventRepository.save(event),
                    getCountConfirmedRequests(event.getId()).orElse(0));
        } else {
            throw new ConflictException("изменить можно только отмененные события или события " +
                    "в состоянии ожидания модерации");
        }
    }

    @Override
    public List<EventFullDto> getEventsByParams(List<Long> users, List<String> states, List<Long> categories, String rangeStart, String rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        LocalDateTime start;
        if (rangeStart == null) {
            start = LocalDateTime.now();
        } else {
            start = LocalDateTime.parse(rangeStart, FORMATTER);
        }
        LocalDateTime end;
        if (rangeStart == null) {
            end = LocalDateTime.of(2099, 12, 30, 22, 22);
        } else {
            end = LocalDateTime.parse(rangeEnd, FORMATTER);
        }
        List<Event> events = eventRepository.findEventsByAdmin(users, states, categories, start, end, pageRequest);
        return events
                .stream()
                .map(event -> EventMapper.eventFullDto(event, getCountConfirmedRequests(event.getId())
                        .orElse(0))).collect(Collectors.toList());
    }

    @Override
    public List<ParticipationRequestDto> getRequestEventByUser(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Список запросов на участие в событие доступен только владельцу аккаунта");
        }
        User user = findUserById(userId);
        return requestRepository.findAllByEventId(event.getId())
                .stream()
                .map(RequestMapper::toRequestEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto confirmRequestForEvent(Long userId, Long eventId, Long reqId) {
        Event event = findEventById(eventId);
        ParticipationRequest request = findRequestById(reqId);
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Подтвердить запрос на участие в событие доступен только владельцу аккаунта");
        }
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException("Запрос не может быть подтвержден");
        }
        request.setStatus(CONFIRMED);
        return RequestMapper.toRequestEventDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatusForEvent(Long userId, Long eventId, ParticipationRequestStatusUpdate participationRequestDto) {
        Event event = findEventById(eventId);
        List<Long> requestIds = participationRequestDto.getRequestIds();
        List<ParticipationRequest> requests = findAllRequestById(requestIds);
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            return null;
        }
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Подтвердить запрос на участие в событие доступен только владельцу аккаунта");
        }
        if (event.getParticipantLimit()
                <= getCountConfirmedRequests(eventId).orElse(0)) {
            throw new ConflictException("достигнут лимит по заявкам на данное событие");
        }
        for (ParticipationRequest request : requests) {
            if (!request.getStatus().equals(RequestEventStatus.PENDING)) {
                throw new ConflictException("статус можно изменить только у заявок, находящихся в состоянии ожидания");
            }
        }
        List<ParticipationRequestDto> confirmRequest = new ArrayList<>();
        List<ParticipationRequestDto> rejectRequest = new ArrayList<>();
        switch (participationRequestDto.getStatus()) {
            case CONFIRMED:
                for (ParticipationRequest request : requests) {
                    if (event.getParticipantLimit()
                            <= getCountConfirmedRequests(eventId).orElse(0)) {
                        request.setStatus(REJECTED);
                        requestRepository.save(request);
                        rejectRequest.add(RequestMapper.toRequestEventDto(request));
                    }
                    request.setStatus(CONFIRMED);
                    requestRepository.save(request);
                    confirmRequest.add(RequestMapper.toRequestEventDto(request));
                }
                break;
            case REJECTED:
                for (ParticipationRequest request : requests) {
                    request.setStatus(REJECTED);
                    requestRepository.save(request);
                    rejectRequest.add(RequestMapper.toRequestEventDto(request));
                }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmRequest)
                .rejectedRequests(rejectRequest)
                .build();
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, EventUpdateRequestDto eventUpdateRequestDto) {
        Event event = findEventById(eventId);
        NewEventDto newEventDto = EventMapper.newEventDto(eventUpdateRequestDto);
        checkBeforeSaveNewEvent(newEventDto, event);
        LocalDateTime updateEventDate = event.getEventDate();
        if (updateEventDate.isBefore(LocalDateTime.now().minusHours(1))) {
            throw new ConflictException("Время события не может быть раньше, чем через два часа");
        }
        if (!event.getState().equals(State.PENDING) && eventUpdateRequestDto.getStateAction()
                .equals(StateAction.PUBLISH_EVENT)) {
            throw new ConflictException("Cобытие можно публиковать, только если оно в состоянии ожидания публикации");
        }
        if (event.getState().equals(State.PENDING) && eventUpdateRequestDto.getStateAction()
                .equals(StateAction.PUBLISH_EVENT)) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
        }
        if (!event.getState().equals(State.PUBLISHED) && eventUpdateRequestDto.getStateAction()
                .equals(StateAction.REJECT_EVENT)) {
            event.setState(State.CANCELED);
            event.setPublishedOn(null);
        }
        if (event.getState().equals(State.PUBLISHED) && eventUpdateRequestDto.getStateAction()
                .equals(StateAction.REJECT_EVENT)) {
            throw new ConflictException("Cобытие можно отклонить, только если оно еще не опубликовано ");
        }
        return EventMapper.eventFullDto(eventRepository.save(event), getCountConfirmedRequests(eventId).orElse(0));
    }

    private void checkBeforeSaveNewEvent(NewEventDto newEventDto, Event event) {
        if (newEventDto.getAnnotation() != null) {
            event.setAnnotation(newEventDto.getAnnotation());
        }
        if (newEventDto.getCategory() != null) {
            event.setCategory(Category.builder().id(newEventDto.getCategory())
                    .build());
        }
        if (newEventDto.getDescription() != null) {
            event.setDescription(newEventDto.getDescription());
        }
        if (newEventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), FORMATTER));
        }
        if (newEventDto.getLocation() != null) {
            Location location = Location.builder()
                    .lat(newEventDto.getLocation().getLat())
                    .lon(newEventDto.getLocation().getLon())
                    .build();
            event.setLocation(location);
        }
        if (newEventDto.getPaid() != null) {
            event.setIsPaid(newEventDto.getPaid());
        }
        if (newEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(newEventDto.getParticipantLimit());
        }
        if (newEventDto.getRequestModeration() != null) {
            event.setRequestModeration(newEventDto.getRequestModeration());
        }
        if (newEventDto.getTitle() != null) {
            event.setTitle(newEventDto.getTitle());
        }
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private Event findEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id " + id + " не найдено"));
    }

    private ParticipationRequest findRequestById(Long reqId) {
        return requestRepository.findById(reqId).orElseThrow(() ->
                new NotFoundException("Реквест с id " + reqId + " не найден"));
    }

    private List<ParticipationRequest> findAllRequestById(List<Long> requestIds) {
        return requestRepository.findAllById(requestIds);
    }

    private Long getViews(long eventId) {
        ResponseEntity<Object> responseEntity = hitClient.getStats(
                LocalDateTime.of(2020, 9, 1, 0, 0).toString(),
                LocalDateTime.now().toString(),
                List.of("/events/" + eventId),false);

        if (Objects.equals(responseEntity.getBody(), "")) {
            return ((Map<String, Long>) responseEntity.getBody()).get("hits");
        }
        return 0L;
    }

    private Optional<Integer> getCountConfirmedRequests(Long eventId) {
        return Optional.of(requestRepository.countParticipationRequestByEventIdAndStatus(eventId,
                RequestEventStatus.CONFIRMED));
    }
}
