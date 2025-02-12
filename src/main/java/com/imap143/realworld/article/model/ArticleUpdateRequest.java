package com.imap143.realworld.article.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

@Getter
public class ArticleUpdateRequest {
    private final Optional<String> title;
    private final Optional<String> description;
    private final Optional<String> body;

    @Builder
    public ArticleUpdateRequest(String title, String description, String body) {
        this.title = Optional.ofNullable(title);
        this.description = Optional.ofNullable(description);
        this.body = Optional.ofNullable(body);
    }

    public boolean hasChanges() {
        return title.isPresent() || description.isPresent() || body.isPresent();
    }
}
