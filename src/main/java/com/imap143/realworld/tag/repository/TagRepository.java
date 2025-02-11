package com.imap143.realworld.tag.repository;

import com.imap143.realworld.tag.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findAll();
    
    Optional<Tag> findByTagName(String tagName);
}
