package ru.practicum.ewm.event.Comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Comment.dto.FixCommentDto;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable Long userId,
                                 @RequestParam Long eventId,
                                 @Valid @RequestBody CommentDto commentDto) {
        CommentDto newComment = commentService.addComment(userId, eventId, commentDto);
        log.info("Пользователь id={} добавил комментарий id={}", userId, newComment.getId());
        return newComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto fixComment(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @Valid @RequestBody FixCommentDto fixCommentDto) {
        CommentDto updComment = commentService.fixComment(userId, commentId, fixCommentDto);
        log.info("Пользователь id={} изменил комментарий id={}", userId, updComment.getId());
        return updComment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
        log.info("Пользователь id={} удалил комментарий id={}", userId, commentId);
    }

}
