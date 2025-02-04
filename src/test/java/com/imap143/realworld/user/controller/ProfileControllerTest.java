package com.imap143.realworld.user.controller;

import com.imap143.realworld.security.CustomUserDetails;
import com.imap143.realworld.security.SecurityConfig;
import com.imap143.realworld.security.jwt.JwtProvider;
import com.imap143.realworld.user.model.Profile;
import com.imap143.realworld.user.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@Import({SecurityConfig.class})
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void getProfile_WithExistingUsername_ReturnsProfile() throws Exception {
        String username = "testuser";
        Profile profile = new Profile(username);
        given(profileService.viewProfile(username))
                .willReturn(Optional.of(profile));

        mockMvc.perform(get("/profiles/{username}", username))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(username))
                .andExpect(jsonPath("$.profile.following").value(false));
    }

    @Test
    void getProfile_WithNonExistingUsername_Returns404() throws Exception {
        String username = "nonexistent";
        given(profileService.viewProfile(username))
                .willReturn(Optional.empty());

        mockMvc.perform(get("/profiles/{username}", username))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void followUser_WithValidRequest_ReturnsUpdatedProfile() throws Exception {
        String username = "usertofollow";
        Profile profile = new Profile(username);
        profile.setFollowing(true);
        
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "testuser");
        given(profileService.followUser(anyString(), anyLong()))
                .willReturn(profile);

        mockMvc.perform(put("/profiles/{username}/follow", username)
                .with(csrf())
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(username))
                .andExpect(jsonPath("$.profile.following").value(true));
    }

    @Test
    void unfollowUser_WithValidRequest_ReturnsUpdatedProfile() throws Exception {
        String username = "usertounfollow";
        Profile profile = new Profile(username);
        profile.setFollowing(false);
        
        CustomUserDetails userDetails = new CustomUserDetails(1L, "test@test.com", "testuser");
        given(profileService.unfollowUser(anyString(), anyLong()))
                .willReturn(profile);

        mockMvc.perform(delete("/profiles/{username}/follow", username)
                .with(csrf())
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.profile.username").value(username))
                .andExpect(jsonPath("$.profile.following").value(false));
    }
} 