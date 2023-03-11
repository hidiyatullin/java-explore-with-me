package ru.practicum.mainService.comments.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainService.comments.dto.CommentDto;
import ru.practicum.mainService.comments.model.Comment;
import ru.practicum.mainService.event.model.Event;
import ru.practicum.mainService.user.model.User;

@Component
@AllArgsConstructor
public class CommentMapper {

    public Comment toComment(CommentDto commentDto, User author, Event event) {
        return Comment.builder()
                .id(commentDto.getId())
                .author(author)
                .event(event)
                .text(commentDto.getText())
                .createdOn(commentDto.getCreatedOn())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .eventId(comment.getEvent().getId())
                .text(comment.getText())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}
