package com.imap143.realworld.article.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.imap143.realworld.article.model.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article save(Article article);
    Optional<Article> findBySlug(String slug);
    void deleteArticleBySlug(String slug);

    Page<Article> findByContent_Tags_TagName(String tagName, Pageable pageable);

    Page<Article> findByAuthor_Profile_Username(String username, Pageable pageable);

    Page<Article> findByFavoritedBy_Profile_Username(String username, Pageable pageable);
}