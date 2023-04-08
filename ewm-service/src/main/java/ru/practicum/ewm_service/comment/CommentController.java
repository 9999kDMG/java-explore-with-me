package ru.practicum.ewm_service.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm_service.comment.dto.CommentDto;
import ru.practicum.ewm_service.comment.dto.NewCommentDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //private part
    @PostMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@NotNull @RequestParam(name = "eventId") Integer eventId,
                                  @RequestBody @Valid NewCommentDto newComment,
                                  @PathVariable Integer userId) {
        return commentService.postCommentByUser(userId, eventId, newComment);
    }

    @PatchMapping("/users/{userId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable Integer userId,
                                    @PathVariable Integer commentId,
                                    @Valid @RequestBody NewCommentDto updateComment) {
        return commentService.updateComment(userId, commentId, updateComment);
    }

    @GetMapping("/users/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getUserComments(@PathVariable Integer userId,
                                            @NotNull
                                            @RequestParam(name = "state", required = false) CommentState state) {
        return commentService.getUserComments(userId, state);
    }

    @GetMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getUserComment(@PathVariable Integer userId,
                                            @PathVariable Integer commentId) {
        return commentService.getUserComment(userId, commentId);
    }

    @DeleteMapping("/users/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer userId, @PathVariable Integer commentId) {
        commentService.deleteComment(userId, commentId);
    }

    //admin part
    @PatchMapping("/admin/comments/{commentId}")
    public CommentDto updateCommentStatus(@PathVariable Integer commentId,
                                          @NotNull
                                          @RequestParam(name = "state", defaultValue = "APPROVED") CommentState state) {
        return commentService.updateCommentByAdmin(commentId, state);
    }

    @DeleteMapping("/admin/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
