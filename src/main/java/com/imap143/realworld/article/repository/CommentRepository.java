package com.imap143.realworld.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imap143.realworld.article.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    void deleteCommentById(Long id);
} 