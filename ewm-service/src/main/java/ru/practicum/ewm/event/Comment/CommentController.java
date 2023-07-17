package ru.practicum.ewm.event.Comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Comment.dto.FixCommentDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{eventId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long eventId,
                                 @RequestBody CommentDto commentDto) {
        CommentDto newComment = commentService.addComment(userId, eventId, commentDto);
        log.info("Пользователь id={} добавил комментарий id={}", userId, newComment.getId());
        return newComment;
    }

    @PatchMapping("/{eventId}/comment/{commentId}")
    public CommentDto fixComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long eventId,
                                 @PathVariable Long commentId,
                                 @RequestBody FixCommentDto fixCommentDto) {
        CommentDto updComment = commentService.fixComment(userId, eventId, commentId, fixCommentDto);
        log.info("Пользователь id={} изменил комментарий id={}", userId, updComment.getId());
        return updComment;
    }

    @DeleteMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        log.info("Пользователь id={} удалил комментарий id={}", userId, commentId);
    }

}
