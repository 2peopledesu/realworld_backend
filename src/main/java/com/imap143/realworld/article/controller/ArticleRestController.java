package com.imap143.realworld.article.controller;

import com.imap143.realworld.article.dto.ArticlePostRequestDTO;
import com.imap143.realworld.article.dto.ArticleResponseDTO;
import com.imap143.realworld.article.dto.ArticleUpdateRequestDTO;
import com.imap143.realworld.article.service.ArticleService;
import com.imap143.realworld.exception.RealWorldException;

import com.imap143.realworld.security.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping(value = "/articles/{slug}")
    public ResponseEntity<ArticleResponseDTO> updateArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ArticleUpdateRequestDTO request) {
        return articleService.update(slug, userDetails.getId(), request)
                .map(article -> ResponseEntity.ok(new ArticleResponseDTO(article, article.getAuthor().getProfile())))
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(RealWorldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRealWorldException(RealWorldException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
