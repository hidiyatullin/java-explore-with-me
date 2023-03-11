package ru.practicum.mainService.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.mainService.comments.dto.CommentDto;
import ru.practicum.mainService.comments.mapper.CommentMapper;
import ru.practicum.mainService.comments.model.Comment;
import ru.practicum.mainService.comments.repository.CommentRepository;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.event.repository.EventRepository;
import ru.practicum.mainService.exeption.NotFoundException;
import ru.practicum.mainService.user.model.User;
import ru.practicum.mainService.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, CommentDto commentDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Comment comment = commentMapper.toComment(commentDto, user, event);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    public CommentDto getComment(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден"));

        return commentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public void deleteById(Long userId, Long commentId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id " + commentId + " не найден"));

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentDto> getAllByEventId(Long eventId, int from, int size) {
        int page = from / size;
        List<Comment> comments =
                commentRepository.findAllCommentByEventId(eventId,
                        PageRequest.of(page, size, Sort.Direction.DESC, "createdOn")).getContent();

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAll(Long userId, Long eventId, int from, int size) {
        int page = from / size;
        List<Comment> allComments = commentRepository.findAll();

        return allComments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
