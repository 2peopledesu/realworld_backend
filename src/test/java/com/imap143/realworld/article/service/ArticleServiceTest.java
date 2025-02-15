package com.imap143.realworld.article.service;

import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.model.ArticleContent;
import com.imap143.realworld.article.repository.ArticleRepository;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.service.UserService;
import com.imap143.realworld.tag.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private TagRepository tagRepository;  // TagRepository Mock 추가

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    void create_WithValidInput_ReturnsArticle() {
        // Given
        long authorId = 1L;
        User author = User.of("author@test.com", "author", null);
        ReflectionTestUtils.setField(author, "id", authorId);

        Set<Tag> tags = new HashSet<>();
        Tag tag = new Tag("tag1");
        tags.add(tag);
        
        ArticleContent content = new ArticleContent(
            "Test Title",
            "Test Description",
            "Test Body",
            tags
        );

        Article expectedArticle = new Article(author, content);
        
        given(userService.findById(authorId)).willReturn(Optional.of(author));
        given(tagRepository.findByTagName(tag.getTagName())).willReturn(Optional.of(tag));  // TagRepository mock 동작 추가
        given(articleRepository.save(any(Article.class))).willReturn(expectedArticle);

        // When
        Article createdArticle = articleService.create(authorId, content);

        // Then
        assertThat(createdArticle).isNotNull();
        assertThat(createdArticle.getContent().getTitle()).isEqualTo("Test Title");
        assertThat(createdArticle.getAuthor()).isEqualTo(author);
        verify(articleRepository).save(any(Article.class));
    }
}