package com.imap143.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.user.model.Profile;
import com.imap143.realworld.user.model.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ArticleResponseDTO {
    private final String slug;
    private final String title;
    private final String description;
    private final String body;
    @JsonProperty("tags")
    private final Set<String> tags;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final boolean favorited;
    @JsonProperty("favoritesCount")
    private final int favoritesCount;
    private final AuthorDTO author;

    public ArticleResponseDTO(Article article, Profile authorProfile, User currentUser) {
        this.slug = article.getSlug();
        this.title = article.getContent().getTitle();
        this.description = article.getContent().getDescription();
        this.body = article.getContent().getBody();
        this.tags = article.getContent().getTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toSet());
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.favorited = currentUser != null && 
                article.getFavoritedBy().stream()
                        .anyMatch(user -> user.getId() == currentUser.getId());
        this.favoritesCount = article.getFavoritesCount();
        this.author = new AuthorDTO(authorProfile, currentUser, article.getAuthor());
    }

    public ArticleResponseDTO(Article article, Profile authorProfile) {
        this(article, authorProfile, null);
    }

    @Getter
    static class AuthorDTO {
        private final String username;
        private final String bio;
        private final String image;
        private final boolean following;

        AuthorDTO(Profile profile, User currentUser, User author) {
            this.username = profile.getUsername();
            this.bio = profile.getBio();
            this.image = profile.getImage();
            this.following = currentUser != null && 
                           author.getFollowers().contains(currentUser);
        }

        AuthorDTO(Profile profile) {
            this(profile, null, null);
        }
    }
}
