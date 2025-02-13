package com.imap143.realworld.article.service;

import com.imap143.realworld.article.model.Comment;
import com.imap143.realworld.user.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imap143.realworld.article.dto.CommentPostRequestDTO;
import com.imap143.realworld.article.model.Article;
import com.imap143.realworld.article.repository.ArticleRepository;
import com.imap143.realworld.article.repository.CommentRepository;
import com.imap143.realworld.exception.RealWorldException;

@Service
public class CommentService {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public CommentService(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public Comment createComment(String slug, User user, CommentPostRequestDTO request) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RealWorldException("Article not found"));

        Comment comment = new Comment(article, user, request.getBody());
        article.addComment(comment);
        
        return commentRepository.save(comment);
    }
}
