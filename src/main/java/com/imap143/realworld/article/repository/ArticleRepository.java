package com.imap143.realworld.article.repository;

import com.imap143.realworld.article.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Article save(Article article);
}
