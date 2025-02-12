package com.imap143.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.article.model.ArticleUpdateRequest;
import lombok.Value;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeName("article")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@Value
public class ArticleUpdateRequestDTO {

    String title;
    String description;
    String body;

    public ArticleUpdateRequest toArticleUpdateContent() {
        return ArticleUpdateRequest.builder()
                .title(title)
                .description(description)
                .body(body)
                .build();
    }

    public boolean hasChanges() {
        return (title != null && !title.trim().isEmpty()) ||
               (description != null && !description.trim().isEmpty()) ||
               (body != null && !body.trim().isEmpty());
    }

    public String getTitleOrNull() {
        return title != null && !title.trim().isEmpty() ? title.trim() : null;
    }

    public String getDescriptionOrNull() {
        return description != null && !description.trim().isEmpty() ? description.trim() : null;
    }

    public String getBodyOrNull() {
        return body != null && !body.trim().isEmpty() ? body.trim() : null;
    }
}
