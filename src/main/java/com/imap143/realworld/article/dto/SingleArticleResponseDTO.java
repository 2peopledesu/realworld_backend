package com.imap143.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.user.model.Profile;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@JsonTypeName("article")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public class SingleArticleResponseDTO {
    private final String slug;
    private final String title;
    private final String description;
    private final String body;
    private final Set<String> tags;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean favorited;
    private final int favoritesCount;
    private final ArticleResponseDTO.AuthorDTO author;

    public SingleArticleResponseDTO(Article article, Profile authorProfile) {
        this(article, authorProfile, null);
    }

    public SingleArticleResponseDTO(Article article, Profile authorProfile, Long currentUserId) {
        this.slug = article.getSlug();
        this.title = article.getContent().getTitle();
        this.description = article.getContent().getDescription();
        this.body = article.getContent().getBody();
        this.tags = article.getContent().getTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.favorited = currentUserId != null &&
                article.getFavoritedBy().stream()
                        .anyMatch(user -> user.getId() == (currentUserId));
        this.favoritesCount = article.getFavoritesCount();
        this.author = new ArticleResponseDTO.AuthorDTO(authorProfile);
    }
}