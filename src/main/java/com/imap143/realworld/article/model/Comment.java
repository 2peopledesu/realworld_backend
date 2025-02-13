package com.imap143.realworld.article.model;

import com.imap143.realworld.user.model.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import lombok.Getter;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Table(name = "comments")
@Entity
@Getter
public class Comment {
    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Article article;

    @JoinColumn(name = "author_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User author;

    @Column(name = "created_at", nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @CreatedDate
    private LocalDateTime updatedAt;

    @Column(name = "body", nullable = false)
    private String body;

    public Comment(Article article, User author, String body) {
        this.article = article;
        this.author = author;
        this.body = body;
    }

    protected Comment() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
