package ru.practicum.mainService.comments.service;

import ru.practicum.mainService.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(Long userId, Long eventId, CommentDto commentDto);

    CommentDto getComment(Long userId, Long commentId);

    void deleteById(Long userId, Long commentId);

    List<CommentDto> getAllByEventId(Long eventId, int from, int size);

    List<CommentDto> getAll(Long userId, Long eventId, int from, int size);
}
