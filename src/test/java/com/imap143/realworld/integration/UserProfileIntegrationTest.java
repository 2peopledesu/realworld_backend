package com.imap143.realworld.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.imap143.realworld.user.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserProfileIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userProfileWorkflow() throws Exception {
        // 1. First user registration
        UserSignUpRequestDto user1SignUp = new UserSignUpRequestDto(
            "user1@test.com", "user1", "password");
        
        String user1Response = mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1SignUp)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String user1Token = extractToken(user1Response);

        // 2. Second user registration
        UserSignUpRequestDto user2SignUp = new UserSignUpRequestDto(
            "user2@test.com", "user2", "password");
        
        mockMvc.perform(post("/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2SignUp)))
                .andExpect(status().isOk());

        // 3. First user views second user's profile
        mockMvc.perform(get("/profiles/user2")
                .header("Authorization", "Token " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value("user2"))
                .andExpect(jsonPath("$.profile.following").value(false));

        // 4. First user follows second user
        mockMvc.perform(put("/profiles/user2/follow")
                .with(csrf())
                .header("Authorization", "Token " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value("user2"))
                .andExpect(jsonPath("$.profile.following").value(true));

        // 5. Update profile
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto(
            null, null, "Updated bio", "new-image.jpg", null);

        mockMvc.perform(put("/user")
                .with(csrf())
                .header("Authorization", "Token " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.bio").value("Updated bio"))
                .andExpect(jsonPath("$.user.image").value("new-image.jpg"));

        // 6. First user unfollows second user
        mockMvc.perform(delete("/profiles/user2/follow")
                .with(csrf())
                .header("Authorization", "Token " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value("user2"))
                .andExpect(jsonPath("$.profile.following").value(false));
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