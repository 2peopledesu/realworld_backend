package com.imap143.realworld.article.model;

import com.imap143.realworld.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;

@Table(name = "articles")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private User author;

    @Embedded
    private ArticleContent content;

    public Article(User author, ArticleContent content) {
        this.author = author;
        this.content = content;
    }

    @Column(unique = true)
    private String slug;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        slug = generateSlug();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateSlug() {
        return content.getTitle().toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9-]", "");
    }

    public void update(String title, String description, String body) {
        if (title != null) {
            this.content = new ArticleContent(
                title,
                description != null ? description : this.content.getDescription(),
                body != null ? body : this.content.getBody(),
                this.content.getTags()
            );
            this.slug = generateSlug();
        } else if (description != null || body != null) {
            this.content = new ArticleContent(
                this.content.getTitle(),
                description != null ? description : this.content.getDescription(),
                body != null ? body : this.content.getBody(),
                this.content.getTags()
            );
        }
    }
}
