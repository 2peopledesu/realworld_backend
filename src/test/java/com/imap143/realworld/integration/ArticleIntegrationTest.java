package com.imap143.realworld.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imap143.realworld.article.dto.ArticlePostRequestDTO;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void articleWorkflow() throws Exception {
        // 1. Create a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto(
            "author@test.com", "author", "password");
        
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = extractToken(signUpResponse);

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO articleRequest = new ArticlePostRequestDTO(
            "How to train your dragon",
            "Ever wonder how?",
            "It takes a Jacobian",
            tags
        );

        mockMvc.perform(post("/articles")
            .with(csrf())
            .header("Authorization", "Token " + token)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(articleRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.article.title").value("How to train your dragon"))
            .andExpect(jsonPath("$.article.description").value("Ever wonder how?"))
            .andExpect(jsonPath("$.article.body").value("It takes a Jacobian"))
            .andExpect(jsonPath("$.article.tags").isArray())
            .andExpect(jsonPath("$.article.tags[0]").exists())
            .andExpect(jsonPath("$.article.author.username").value("author"))
            .andExpect(jsonPath("$.article.favoritesCount").isNumber());
    }

    private String extractToken(String response) {
        try {
            return objectMapper.readTree(response)
                    .path("user")
                    .path("token")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract token", e);
        }
    }
} 