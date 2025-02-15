package com.imap143.realworld.article.model;

import java.util.HashSet;
import java.util.Set;

import com.imap143.realworld.tag.model.Tag;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class ArticleContent {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String body;

    @JoinTable(name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Tag> tags = new HashSet<>();

    public ArticleContent(String title, String description, String body, Set<Tag> tags) {
        this.title = title;
        this.description = description;
        this.body = body;
        this.tags = tags;
    }

    protected ArticleContent() {}

}
