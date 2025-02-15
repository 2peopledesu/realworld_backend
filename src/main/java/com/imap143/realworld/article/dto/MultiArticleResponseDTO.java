package com.imap143.realworld.article.dto;

import com.imap143.realworld.article.model.Article;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MultiArticleResponseDTO {
    private final List<ArticleResponseDTO> articles;
    private final int articlesCount;

    private MultiArticleResponseDTO(List<ArticleResponseDTO> articles) {
        this.articles = articles;
        this.articlesCount = articles.size();
    }

    public static MultiArticleResponseDTO of(Page<Article> articlePage) {
        List<ArticleResponseDTO> articles = articlePage.getContent().stream()
                .map(article -> new ArticleResponseDTO(
                        article,
                        article.getAuthor().getProfile()))
                .collect(Collectors.toList());

        return new MultiArticleResponseDTO(articles);
    }
}