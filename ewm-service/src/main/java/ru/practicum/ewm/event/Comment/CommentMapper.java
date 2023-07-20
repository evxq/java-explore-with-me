package ru.practicum.ewm.event.Comment;

import ru.practicum.ewm.event.Comment.dto.CommentDto;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .eventTitle(comment.getEvent().getTitle())
                .created(comment.getCreated()).build();
    }

    public static Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated()).build();
    }

}
