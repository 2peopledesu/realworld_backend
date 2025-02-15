package com.imap143.realworld.article.model;

import com.imap143.realworld.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<Comment> comments = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "article_favorites",
        joinColumns = @JoinColumn(name = "article_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> favoritedBy = new HashSet<>();

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
        if (title != null || description != null || body != null) {
            this.content = new ArticleContent(
                title != null ? title : this.content.getTitle(),
                description != null ? description : this.content.getDescription(),
                body != null ? body : this.content.getBody(),
                this.content.getTags()
            );
            if (title != null) {
                this.slug = generateSlug();
            }
        }
    }
    
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
    }

    public Article addFavorite(User user) {
        this.favoritedBy.add(user);
        return this;
    }

    public Article removeFavorite(User user) {
        this.favoritedBy.remove(user);
        return this;
    }

    public boolean isFavoritedBy(User user) {
        return this.favoritedBy.contains(user);
    }

    public int getFavoritesCount() {
        return this.favoritedBy.size();
    }
}
