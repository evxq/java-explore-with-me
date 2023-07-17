package ru.practicum.ewm.event.Comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.Comment.dto.CommentDto;
import ru.practicum.ewm.event.Comment.dto.FixCommentDto;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.WrongParameterException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            log.info("Комментарий не может быть пустым");
            throw new WrongParameterException("Комментарий не может быть пустым");
        }
        User author = checkUserForExist(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("Event with id={} was not found", eventId);
                    throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
                });
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setEvent(event);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto fixComment(Long userId, Long eventId, Long commentId, FixCommentDto fixCommentDto) {
        if (fixCommentDto.getText().isEmpty()) {
            log.info("Комментарий не может быть пустым");
            throw new WrongParameterException("Комментарий не может быть пустым");
        }
        User author = checkUserForExist(userId);
        Comment existComment = checkCommentForExistAndAuthor(commentId, author);
        if (fixCommentDto.getText() != null) {
            existComment.setText(fixCommentDto.getText());
        }
        return CommentMapper.toCommentDto(commentRepository.save(existComment));
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        User author = checkUserForExist(userId);
        Comment existComment = checkCommentForExistAndAuthor(commentId, author);
        commentRepository.delete(existComment);
    }

    private User checkUserForExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User with id={} was not found", userId);
                    throw new NotFoundException(String.format("User with id=%d was not found", userId));
                });
    }

    private Comment checkCommentForExistAndAuthor(Long commentId, User author) {
         Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment with id={} was not found", commentId);
                    throw new NotFoundException(String.format("Comment with id=%d was not found", commentId));
                });
        if (!comment.getAuthor().equals(author)) {
            log.info("Пользователь id={} не является автором комментария id={}", author.getId(), commentId);
            throw new WrongParameterException("Только автор может изменить комментарий");
        }
        return comment;
    }

}
