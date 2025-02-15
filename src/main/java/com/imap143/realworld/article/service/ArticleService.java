package com.imap143.realworld.article.service;

import com.imap143.realworld.article.dto.ArticleUpdateRequestDTO;
import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.model.ArticleContent;
import com.imap143.realworld.article.repository.ArticleRepository;
import com.imap143.realworld.exception.RealWorldException;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.tag.repository.TagRepository;
import com.imap143.realworld.user.service.UserService;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArticleService {
    private final UserService userService;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public ArticleService(UserService userService, TagRepository tagRepository, ArticleRepository articleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Article create(long AuthorId, ArticleContent articleContent) {
        Set<Tag> newTags = articleContent.getTags().stream()
                .map(tag -> tagRepository.findByTagName(tag.getTagName())
                        .orElseGet(() -> tagRepository.save(tag)))
                .collect(Collectors.toSet());

        articleContent.setTags(newTags);
        
        return userService.findById(AuthorId)
                .map(user -> articleRepository.save(new Article(user, articleContent)))
                .orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<Article> findBySlug(String slug) {
        return articleRepository.findBySlug(slug);
    }

    @Transactional(readOnly = true)
    public Page<Article> findAll(Pageable pageable) {
        return articleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> findByTag(String tag, Pageable pageable) {
        return articleRepository.findByContent_Tags_TagName(tag, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> findByAuthor(String author, Pageable pageable) {
        return articleRepository.findByAuthor_Profile_Username(author, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Article> findByFavorited(String username, Pageable pageable) {
        return articleRepository.findByFavoritedBy_Profile_Username(username, pageable);
    }

    @Transactional
    public Article addFavorite(String slug, Long userId) {
        Article article = findBySlug(slug)
                .orElseThrow(() -> new RealWorldException("Article not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RealWorldException("User not found"));
        
        if (article.getFavoritedBy().stream().anyMatch(u -> u.getId() == (userId))) {
            throw new RealWorldException("Article is already favorited");
        }
        
        article.addFavorite(user);
        return articleRepository.save(article);
    }

    @Transactional
    public Article unFavorite(String slug, Long userId) {
        Article article = findBySlug(slug)
                .orElseThrow(() -> new RealWorldException("Article not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RealWorldException("User not found"));
        
        if (article.getFavoritedBy().stream().noneMatch(u -> u.getId() == (userId))) {
            throw new RealWorldException("Article is not favorited yet");
        }
        
        article.removeFavorite(user);
        return articleRepository.save(article);
    }

    @Transactional
    public Optional<Article> update(String slug, long userId, ArticleUpdateRequestDTO request) {
        if (!request.hasChanges()) {
            throw new RealWorldException("At least one field must be provided for update");
        }

        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RealWorldException("Article not found"));

        if (article.getAuthor().getId() != userId) {
            throw new RealWorldException("You can only update your own articles");
        }

        article.update(
            request.getTitleOrNull(),
            request.getDescriptionOrNull(),
            request.getBodyOrNull()
        );
        
        return Optional.of(article);
    }

    @Transactional
    public void delete(String slug, long userId) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RealWorldException("Article not found"));

        if (article.getAuthor().getId() != userId) {
            throw new RealWorldException("You can only delete your own articles");
        }

        articleRepository.delete(article);
    }

    @Transactional(readOnly = true)
    public Page<Article> getFeed(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RealWorldException("User not found"));

        return articleRepository.findByAuthorInOrderByCreatedAtDesc(user.getFollowing(), pageable);
    }
}
