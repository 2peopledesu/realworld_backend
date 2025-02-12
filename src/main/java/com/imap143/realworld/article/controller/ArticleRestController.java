package com.imap143.realworld.article.controller;

import com.imap143.realworld.article.dto.ArticlePostRequestDTO;
import com.imap143.realworld.article.dto.ArticleResponseDTO;
import com.imap143.realworld.article.service.ArticleService;

import com.imap143.realworld.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArticleRestController {
    private final ArticleService articleService;

    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping(value = "/articles")
    public ResponseEntity<ArticleResponseDTO> createArticle(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ArticlePostRequestDTO request) {
        final var newArticle = articleService.create(userDetails.getId(), request.toArticleContent());
        return ResponseEntity.ok(new ArticleResponseDTO(newArticle, newArticle.getAuthor().getProfile()));
    }

    @GetMapping(value = "/articles/{slug}")
    public ResponseEntity<ArticleResponseDTO> getArticle(@PathVariable String slug) {
        return articleService.findBySlug(slug)
                .map(article -> ResponseEntity.ok(new ArticleResponseDTO(article, article.getAuthor().getProfile())))
                .orElse(ResponseEntity.notFound().build());
    }
}
