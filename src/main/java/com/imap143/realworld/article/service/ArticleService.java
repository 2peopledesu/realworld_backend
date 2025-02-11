package com.imap143.realworld.article.service;

import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.model.ArticleContent;
import com.imap143.realworld.article.repository.ArticleRepository;
import com.imap143.realworld.tag.service.TagService;
import com.imap143.realworld.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleService {
    private final UserService userService;
    private final TagService tagService;
    private final ArticleRepository articleRepository;

    public ArticleService(UserService userService, TagService tagService, ArticleRepository articleRepository) {
        this.userService = userService;
        this.tagService = tagService;
        this.articleRepository = articleRepository;
    }

    @Transactional
    public Article create(long AuthorId, ArticleContent articleContent) {
        final var getExistingTags = tagService.refreshTagsIfExists(articleContent.getTags());
        articleContent.setTags(getExistingTags);
        return userService.findById(AuthorId)
                .map(user -> articleRepository.save(new Article(user, articleContent)))
                .orElseThrow();
    }
}
