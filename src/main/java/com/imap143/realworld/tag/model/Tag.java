package com.imap143.realworld.tag.model;

import jakarta.persistence.*;
import lombok.Getter;

@Table(name = "tags")
@Entity
@Getter
public class Tag {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @Column(name = "tag_name", nullable = false, unique = true)
    private String tagName;

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public Tag() {}
}
