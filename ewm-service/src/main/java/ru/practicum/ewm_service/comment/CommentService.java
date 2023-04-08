package ru.practicum.ewm_service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm_service.comment.dto.CommentDto;
import ru.practicum.ewm_service.comment.dto.NewCommentDto;
import ru.practicum.ewm_service.event.Event;
import ru.practicum.ewm_service.event.EventService;
import ru.practicum.ewm_service.exception.BadRequestException;
import ru.practicum.ewm_service.exception.ConflictException;
import ru.practicum.ewm_service.exception.NotFoundException;
import ru.practicum.ewm_service.user.User;
import ru.practicum.ewm_service.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository commentRepository;

    public CommentDto postCommentByUser(Integer userId, Integer eventId, NewCommentDto newComment) {
        if (newComment.getText() == null) {
            throw new BadRequestException("the comment does not contain text");
        }
        User user = userService.getUserOrThrow(userId);
        Event event = eventService.getEventOrThrow(eventId);
        Comment comment = Comment.builder()
                .text(newComment.getText())
                .user(user)
                .created(LocalDateTime.now())
                .state(CommentState.PENDING)
                .event(event)
                .build();

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public CommentDto updateComment(Integer userId, Integer commentId, NewCommentDto updateComment) {
        userService.getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);

        if (updateComment.getText() == null) {
            throw new BadRequestException("the comment does not contain text");
        }

        throwIfUserNorAuthor(userId, comment);

        comment.setText(updateComment.getText());
        comment.setState(CommentState.PENDING);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> getUserComments(Integer userId, CommentState state) {
        userService.getUserOrThrow(userId);
        List<Comment> comments = new ArrayList<>();

        if (state == null) {
            comments = commentRepository.findAll();
        } else {
            switch (state) {
                case PENDING:
                    comments = commentRepository.findAllByState(CommentState.PENDING);
                    break;
                case APPROVED:
                    comments = commentRepository.findAllByState(CommentState.APPROVED);
                    break;
                case REJECTED:
                    comments = commentRepository.findAllByState(CommentState.REJECTED);
                    break;
            }
        }
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    public CommentDto getUserComment(Integer userId, Integer commentId) {
        userService.getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);

        throwIfUserNorAuthor(userId, comment);

        return CommentMapper.toCommentDto(comment);
    }


    public void deleteComment(Integer userId, Integer commentId) {
        userService.getUserOrThrow(userId);
        Comment comment = getCommentOrThrow(commentId);
        throwIfUserNorAuthor(userId, comment);

        commentRepository.delete(comment);
    }

    public CommentDto updateCommentByAdmin(Integer commentId, CommentState state) {
        Comment comment = getCommentOrThrow(commentId);
        comment.setState(state);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    public void deleteCommentByAdmin(Integer commentId) {
        Comment comment = getCommentOrThrow(commentId);

        commentRepository.delete(comment);
    }

    private Comment getCommentOrThrow(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("comment id N%s", id)));
    }

    private void throwIfUserNorAuthor(Integer userId, Comment comment) {
        if (!Objects.equals(comment.getUser().getId(), userId)) {
            throw new ConflictException("the user is not the author");
        }
    }
}
