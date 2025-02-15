package com.imap143.realworld.article.controller;

import com.imap143.realworld.article.dto.CommentPostRequestDTO;
import com.imap143.realworld.article.dto.CommentResponseDTO;
import com.imap143.realworld.article.model.Comment;
import com.imap143.realworld.exception.RealWorldException;
import com.imap143.realworld.security.CustomUserDetails;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.imap143.realworld.article.service.CommentService;

@RestController
public class CommentRestController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentRestController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping(value = "/articles/{slug}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CommentPostRequestDTO request) {
        
        User user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new RealWorldException("User not found"));
                
        Comment comment = commentService.createComment(slug, user, request);
        return ResponseEntity.ok(new CommentResponseDTO(comment, comment.getAuthor().getProfile()));
    }

    @DeleteMapping("/articles/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.deleteComment(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
