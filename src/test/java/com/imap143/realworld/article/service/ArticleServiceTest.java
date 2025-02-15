package com.imap143.realworld.article.service;

import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.model.ArticleContent;
import com.imap143.realworld.article.repository.ArticleRepository;
import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.repository.UserRepository;
import com.imap143.realworld.user.service.UserService;
import com.imap143.realworld.tag.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class ArticleServiceTest {
    private ArticleService articleService;
    private ArticleRepository articleRepository;
    private UserRepository userRepository;
    private UserService userService;
    private TagService tagService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        articleRepository = mock(ArticleRepository.class);
        userRepository = mock(UserRepository.class);
        userService = mock(UserService.class);
        tagService = mock(TagService.class);
        passwordEncoder = new BCryptPasswordEncoder();
        articleService = new ArticleService(userService, tagService, articleRepository, userRepository);
    }

    @Test
    void getFeed_ShouldReturnArticlesFromFollowedUsers() {
        Long currentUserId = 1L;

        Password currentPassword = Password.of("password123", passwordEncoder);
        Password followedPassword = Password.of("password123", passwordEncoder);

        User currentUser = User.of("user@test.com", "user", currentPassword);
        User followedUser = User.of("followed@test.com", "followed", followedPassword);

        ArticleContent content = new ArticleContent("Test Title", "Test Description", "Test Body", new HashSet<>());
        Article article = new Article(followedUser, content);

        currentUser.follow(followedUser);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(articleRepository.findByAuthorInOrderByCreatedAtDesc(currentUser.getFollowing(), PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(Collections.singletonList(article)));

        Page<Article> result = articleService.getFeed(currentUserId, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAuthor()).isEqualTo(followedUser);

        verify(userRepository).findById(currentUserId);
        verify(articleRepository).findByAuthorInOrderByCreatedAtDesc(any(), any(Pageable.class));
    }

    @Test
    void getFeed_WithNoFollowedUsers_ShouldReturnEmptyPage() {
        Long currentUserId = 1L;
        Password password = Password.of("password123", passwordEncoder);
        User currentUser = User.of("user@test.com", "user", password);

        when(userRepository.findById(currentUserId)).thenReturn(Optional.of(currentUser));
        when(articleRepository.findByAuthorInOrderByCreatedAtDesc(currentUser.getFollowing(), PageRequest.of(0, 20)))
                .thenReturn(Page.empty());

        Page<Article> result = articleService.getFeed(currentUserId, PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();

        verify(userRepository).findById(currentUserId);
        verify(articleRepository).findByAuthorInOrderByCreatedAtDesc(any(), any(Pageable.class));
    }
} 