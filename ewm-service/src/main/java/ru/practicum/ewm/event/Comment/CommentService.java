package ru.practicum.ewm.event.Comment;

import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Comment.dto.FixCommentDto;

public interface CommentService {

    CommentDto addComment(Long userId, Long eventId, CommentDto commentDto);

    CommentDto fixComment(Long userId, Long eventId, Long commentId, FixCommentDto fixCommentDto);

    void deleteComment(Long userId, Long commentId);

}
