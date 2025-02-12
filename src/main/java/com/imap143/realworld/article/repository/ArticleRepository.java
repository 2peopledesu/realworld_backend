package com.imap143.realworld.article.repository;

import com.imap143.realworld.article.model.Article;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article save(Article article);
    Optional<Article> findBySlug(String slug);
}
