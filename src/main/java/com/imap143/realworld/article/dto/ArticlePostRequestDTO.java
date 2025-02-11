package com.imap143.realworld.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.imap143.realworld.article.model.ArticleContent;
import com.imap143.realworld.tag.model.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.util.Set;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeName("article")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@Value
public class ArticlePostRequestDTO {

    @NotBlank
    String title;
    @NotBlank
    String description;
    @NotBlank
    String body;
    @NotNull
    @JsonProperty("tags")
    Set<Tag> tagList;

    public ArticleContent toArticleContent() {
        return new ArticleContent(title, description, body, tagList);
    }
}
