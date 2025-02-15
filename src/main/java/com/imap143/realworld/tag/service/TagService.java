package com.imap143.realworld.tag.service;

import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.tag.repository.TagRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public Set<Tag> findAll() {
        return new HashSet<>(tagRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Set<Tag> refreshTagsIfExists(Set<Tag> tags) {
        return tags.stream()
                .<Tag>map(tag -> tagRepository.findByTagName(tag.getTagName())
                        .orElse(tag))
                .collect(Collectors.toSet());
    }
}
