package com.imap143.realworld.article.model;

import com.imap143.realworld.article.dto.ArticleResponseDTO;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;


@Getter
public class MultiArticle {
    private final List<ArticleResponseDTO> articles;
    private final int articlesCount;

    private MultiArticle(List<ArticleResponseDTO> articles, int totalCount) {
        this.articles = articles;
        this.articlesCount = totalCount;
    }

    public static MultiArticle of(Page<Article> articlePage) {
        List<ArticleResponseDTO> articles = articlePage.getContent().stream()
                .map(article -> new ArticleResponseDTO(
                        article,
                        article.getAuthor().getProfile()))
                .collect(Collectors.toList());
        
        return new MultiArticle(articles, (int) articlePage.getTotalElements());
    }
}