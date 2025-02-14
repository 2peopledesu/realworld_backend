package com.imap143.realworld.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imap143.realworld.article.dto.ArticlePostRequestDTO;
import com.imap143.realworld.article.dto.ArticleUpdateRequestDTO;
import com.imap143.realworld.article.dto.CommentPostRequestDTO;
import com.imap143.realworld.exception.RealWorldException;
import com.imap143.realworld.tag.model.Tag;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ArticleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createAndGetArticle() throws Exception {
        // 1. Create a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

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

        // 3. Get the article
        mockMvc.perform(get("/articles/how-to-train-your-dragon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value("how-to-train-your-dragon"))
                .andExpect(jsonPath("$.article.title").value("How to train your dragon"))
                .andExpect(jsonPath("$.article.description").value("Ever wonder how?"))
                .andExpect(jsonPath("$.article.body").value("It takes a Jacobian"))
                .andExpect(jsonPath("$.article.tags").isArray())
                .andExpect(jsonPath("$.article.author.username").value("author"))
                .andExpect(jsonPath("$.article.author.following").value(false));
    }

    @Test
    void updateArticle() throws Exception {
        // 1. Create a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(signUpResponse);

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO createRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // 3. Update the article
        ArticleUpdateRequestDTO updateRequest = new ArticleUpdateRequestDTO(
                "How to train your dragon 2",
                "New description",
                "With new content"
        );

        mockMvc.perform(put("/articles/how-to-train-your-dragon")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.title").value("How to train your dragon 2"))
                .andExpect(jsonPath("$.article.description").value("New description"))
                .andExpect(jsonPath("$.article.body").value("With new content"))
                .andExpect(jsonPath("$.article.slug").value("how-to-train-your-dragon-2"));
    }

    @Test
    void updateArticleWithEmptyFields() throws Exception {
        // 1. Create a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(signUpResponse);

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO createRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // Try to update with empty fields
        ArticleUpdateRequestDTO emptyUpdateRequest = new ArticleUpdateRequestDTO("", "", "");

        mockMvc.perform(put("/articles/how-to-train-your-dragon")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("At least one field must be provided for update"));

        // Try to update with null fields
        ArticleUpdateRequestDTO partialUpdateRequest = new ArticleUpdateRequestDTO(
                null,
                "Updated description",
                null
        );

        mockMvc.perform(put("/articles/how-to-train-your-dragon")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(partialUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.title").value("How to train your dragon"))
                .andExpect(jsonPath("$.article.description").value("Updated description"))
                .andExpect(jsonPath("$.article.body").value("It takes a Jacobian"));
    }

    @Test
    void deleteArticle() throws Exception {
        // 1. Register a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(signUpResponse);

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO createRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // 3. Delete the article
        mockMvc.perform(delete("/articles/how-to-train-your-dragon")
                .with(csrf())
                .header("Authorization", "Token " + token))
                .andExpect(status().isNoContent());

        // 4. Try to get the article
        mockMvc.perform(get("/articles/how-to-train-your-dragon"))
                .andExpect(status().isNotFound());

        // 5. Try to delete the article again
        mockMvc.perform(delete("/articles/non-existent-article")
                .with(csrf())
                .header("Authorization", "Token " + token))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Article not found"));
    }

    @Test
    void createComment() throws Exception {
        // 1. Create a user
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = extractToken(signUpResponse);

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO createRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk());

        // 3. Create a comment
        CommentPostRequestDTO commentRequest = new CommentPostRequestDTO("Great article!");
        
        mockMvc.perform(post("/articles/how-to-train-your-dragon/comments")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment.body").value("Great article!"))
                .andExpect(jsonPath("$.comment.author.username").value("author"));
    }

    @Test
    void deleteComment() throws Exception {
        // 1. Register user and get token
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String signUpResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = JsonPath.read(signUpResponse, "$.user.token");

        // 2. Create article
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
                .andExpect(status().isOk());

        // 3. Create comment
        CommentPostRequestDTO commentRequest = new CommentPostRequestDTO("Great article!");
        
        MvcResult result = mockMvc.perform(post("/articles/how-to-train-your-dragon/comments")
                .with(csrf())
                .header("Authorization", "Token " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract comment ID from response
        String responseBody = result.getResponse().getContentAsString();
        Number commentIdNum = JsonPath.read(responseBody, "$.comment.id");
        Long commentId = commentIdNum.longValue();  // Convert Number to Long

        // 4. Delete comment
        mockMvc.perform(delete("/articles/comments/" + commentId)
                .with(csrf())
                .header("Authorization", "Token " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_NotAuthor() throws Exception {
        // 1. Register author and get token
        UserSignUpRequestDto authorSignUp = new UserSignUpRequestDto("author@test.com", "author", "password123");
        String authorResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authorSignUp)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String authorToken = JsonPath.read(authorResponse, "$.user.token");

        // 2. Register another user
        UserSignUpRequestDto otherSignUp = new UserSignUpRequestDto("other@test.com", "other", "password123");
        String otherResponse = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherSignUp)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String otherToken = JsonPath.read(otherResponse, "$.user.token");

        // 3. Create article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO articleRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
                .with(csrf())
                .header("Authorization", "Token " + authorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isOk());

        // 4. Create comment
        CommentPostRequestDTO commentRequest = new CommentPostRequestDTO("Great article!");
        
        MvcResult result = mockMvc.perform(post("/articles/how-to-train-your-dragon/comments")
                .with(csrf())
                .header("Authorization", "Token " + authorToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // Extract comment ID
        String responseBody = result.getResponse().getContentAsString();
        Number commentIdNum = JsonPath.read(responseBody, "$.comment.id");
        Long commentId = commentIdNum.longValue();

        // 5. Try to delete comment with different user - expect 403 Forbidden
        mockMvc.perform(delete("/articles/comments/" + commentId)
                .with(csrf())
                .header("Authorization", "Token " + otherToken))
                .andExpect(status().isForbidden())
                .andExpect(resultActions -> {
                    assertTrue(resultActions.getResolvedException() instanceof RealWorldException);
                    assertEquals("Only comment author can delete the comment", 
                        resultActions.getResolvedException().getMessage());
                });
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