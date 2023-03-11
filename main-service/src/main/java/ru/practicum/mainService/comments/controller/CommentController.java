package ru.practicum.mainService.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.comments.dto.CommentDto;
import ru.practicum.mainService.comments.service.CommentService;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{id}/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("id") Long userId,
                                    @RequestParam Long eventId,
                                    @RequestBody @Valid CommentDto commentDto) {
        return commentService.create(userId, eventId, commentDto);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable("id") Long userId,
                                     @PathVariable Long commentId) {
        return commentService.getComment(userId, commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable("id") Long userId,
                           @PathVariable Long commentId) {
        commentService.deleteById(userId, commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllByEventId(@RequestParam Long eventId,
                                            @RequestParam(name = "from", defaultValue = "0") int from,
                                            @RequestParam(name = "size", defaultValue = "10") int size) {
        return commentService.getAllByEventId(eventId, from, size);
    }

}
