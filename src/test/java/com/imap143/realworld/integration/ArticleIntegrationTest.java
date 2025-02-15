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
import java.util.stream.Collectors;

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
import static org.hamcrest.Matchers.containsInAnyOrder;

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
        String token = createUserAndGetToken("author@test.com", "author", "password123");

        // 2. Create an article
        Set<Tag> tags = Set.of(new Tag("dragons"), new Tag("training"));
        ArticlePostRequestDTO articleRequest = new ArticlePostRequestDTO(
                "How to train your dragon",
                "Ever wonder how?",
                "It takes a Jacobian",
                tags
        );

        mockMvc.perform(post("/articles")
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
        String slug = "how-to-train-your-dragon";
        mockMvc.perform(get("/articles/{slug}", slug))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.slug").value(slug))
                .andExpect(jsonPath("$.article.title").value("How to train your dragon"));
    }

    @Test
    void updateArticle() throws Exception {
        // 1. Create a user and get token
        String token = createUserAndGetToken("author@test.com", "author", "password123");

        // 2. Create an article
        createArticle(token, "How to train your dragon", "Ever wonder how?", "It takes a Jacobian",
                Set.of("dragons", "training"));

        // 3. Update the article
        ArticleUpdateRequestDTO updateRequest = new ArticleUpdateRequestDTO(
                "How to train your dragon 2",
                "New description",
                "With new content"
        );

        mockMvc.perform(put("/articles/how-to-train-your-dragon")
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
        // 1. Create a user and get token
        String token = createUserAndGetToken("author@test.com", "author", "password123");

        // 2. Create an article
        createArticle(token, "How to train your dragon", "Ever wonder how?", "It takes a Jacobian",
                Set.of("dragons", "training"));

        // 3. Try to update with empty fields
        ArticleUpdateRequestDTO emptyUpdate = new ArticleUpdateRequestDTO("", "", "");
        mockMvc.perform(put("/articles/how-to-train-your-dragon")
                        .header("Authorization", "Token " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("At least one field must be provided for update"));

        // 4. Update only description
        ArticleUpdateRequestDTO partialUpdate = new ArticleUpdateRequestDTO(
                null, "Updated description", null);
        mockMvc.perform(put("/articles/how-to-train-your-dragon")
                        .header("Authorization", "Token " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
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

    @Test
    void getArticlesWithParameters() throws Exception {
        // 1. Create two users
        String author1Token = createUserAndGetToken("author1@test.com", "author1", "password123");
        String author2Token = createUserAndGetToken("author2@test.com", "author2", "password123");

        // 2. First user creates articles
        createArticle(author1Token, "First Article", "Description 1", "Body 1", Set.of("tag1", "tag2"));
        createArticle(author1Token, "Second Article", "Description 2", "Body 2", Set.of("tag2", "tag3"));

        // 3. Second user creates an article
        createArticle(author2Token, "Third Article", "Description 3", "Body 3", Set.of("tag1", "tag3"));

        // 4. Test filtering by tag
        mockMvc.perform(get("/articles")
                        .param("tag", "tag1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2))
                .andExpect(jsonPath("$.articles[0].tags", containsInAnyOrder("tag1", "tag2")));

        // 5. Test filtering by author
        mockMvc.perform(get("/articles")
                        .param("author", "author1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(2))
                .andExpect(jsonPath("$.articles[0].author.username").value("author1"));

        // 6. Test filtering by favorited articles
        String slug = "first-article";
        mockMvc.perform(post("/articles/" + slug + "/favorite")
                        .header("Authorization", "Token " + author2Token))
                .andExpect(status().isOk());

        mockMvc.perform(get("/articles")
                        .param("favorited", "author2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articlesCount").value(1))
                .andExpect(jsonPath("$.articles[0].slug").value(slug));
    }

    @Test
    void favoriteAndUnfavoriteArticle() throws Exception {
        // 1. Create two users: author and reader
        String authorToken = createUserAndGetToken("author@test.com", "author", "password123");
        String readerToken = createUserAndGetToken("reader@test.com", "reader", "password123");

        // 2. Create an article
        createArticle(authorToken, "Test Article", "Description", "Body", Set.of("tag1"));
        String slug = "test-article";

        // 3. Test favoriting an article
        mockMvc.perform(post("/articles/" + slug + "/favorite")
                        .header("Authorization", "Token " + readerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.favorited").value(true))
                .andExpect(jsonPath("$.article.favoritesCount").value(1));

        // 4. Test favoriting an already favorited article
        mockMvc.perform(post("/articles/" + slug + "/favorite")
                        .header("Authorization", "Token " + readerToken))
                .andExpect(status().isBadRequest());

        // 5. Test unfavoriting an article
        mockMvc.perform(delete("/articles/" + slug + "/favorite")
                        .header("Authorization", "Token " + readerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.article.favorited").value(false))
                .andExpect(jsonPath("$.article.favoritesCount").value(0));

        // 6. Test unfavoriting a non-favorited article
        mockMvc.perform(delete("/articles/" + slug + "/favorite")
                        .header("Authorization", "Token " + readerToken))
                .andExpect(status().isBadRequest());
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

    private String createUserAndGetToken(String email, String username, String password) throws Exception {
        UserSignUpRequestDto signUpRequest = new UserSignUpRequestDto(email, username, password);
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.user.token");
    }

    private void createArticle(String token, String title, String description, String body, Set<String> tags) throws Exception {
        ArticlePostRequestDTO request = new ArticlePostRequestDTO(title, description, body,
                tags.stream().map(Tag::new).collect(Collectors.toSet()));

        mockMvc.perform(post("/articles")
                        .header("Authorization", "Token " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
} 