package com.imap143.realworld.article.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.article.model.Comment;
import com.imap143.realworld.user.model.Profile;

import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Getter
@JsonTypeName("comment")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class CommentResponseDTO {
    private final long id;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String body;
    private final Profile author;

    public CommentResponseDTO(Comment comment, Profile authorProfile) {
        this.id = comment.getId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.body = comment.getBody();
        this.author = authorProfile;
    }
}
