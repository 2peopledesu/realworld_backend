package com.imap143.realworld.article.controller;

import com.imap143.realworld.article.dto.ArticlePostRequestDTO;
import com.imap143.realworld.article.dto.ArticleUpdateRequestDTO;
import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.service.ArticleService;
import com.imap143.realworld.exception.RealWorldException;

import com.imap143.realworld.security.CustomUserDetails;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import com.imap143.realworld.article.dto.MultiArticleResponseDTO;
import com.imap143.realworld.article.dto.SingleArticleResponseDTO;

@RestController
public class ArticleRestController {
    private final ArticleService articleService;

    public ArticleRestController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping(value = "/articles")
    public MultiArticleResponseDTO getArticles(
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String favorited,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        Pageable pageable = PageRequest.of(offset/limit, limit);
        
        if (tag != null) {
            return MultiArticleResponseDTO.of(articleService.findByTag(tag, pageable));
        }
        if (author != null) {
            return MultiArticleResponseDTO.of(articleService.findByAuthor(author, pageable));
        }
        if (favorited != null) {
            return MultiArticleResponseDTO.of(articleService.findByFavorited(favorited, pageable));
        }
        
        return MultiArticleResponseDTO.of(articleService.findAll(pageable));
    }

    @PostMapping(value = "/articles")
    public SingleArticleResponseDTO createArticle(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody ArticlePostRequestDTO request) {
        final var newArticle = articleService.create(userDetails.getId(), request.toArticleContent());
        return new SingleArticleResponseDTO(newArticle, newArticle.getAuthor().getProfile());
    }

    @GetMapping(value = "/articles/{slug}")
    public ResponseEntity<SingleArticleResponseDTO> getArticle(@PathVariable String slug) {
        return articleService.findBySlug(slug)
                .map(article -> ResponseEntity.ok(new SingleArticleResponseDTO(article, article.getAuthor().getProfile())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/articles/{slug}")
    public ResponseEntity<SingleArticleResponseDTO> updateArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody ArticleUpdateRequestDTO request) {
        
        return articleService.update(slug, userDetails.getId(), request)
                .map(article -> ResponseEntity.ok(new SingleArticleResponseDTO(article, article.getAuthor().getProfile())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(value = "/articles/{slug}")
    public ResponseEntity<Void> deleteArticle(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        articleService.delete(slug, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/articles/{slug}/favorite")
    public SingleArticleResponseDTO addFavorite(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Article article = articleService.addFavorite(slug, userDetails.getId());
        return new SingleArticleResponseDTO(article, article.getAuthor().getProfile(), userDetails.getId());
    }

    @DeleteMapping(value = "/articles/{slug}/favorite")
    public SingleArticleResponseDTO unFavorite(
            @PathVariable String slug,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Article article = articleService.unFavorite(slug, userDetails.getId());
        return new SingleArticleResponseDTO(article, article.getAuthor().getProfile(), userDetails.getId());
    }

    @ExceptionHandler(RealWorldException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleRealWorldException(RealWorldException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
}
