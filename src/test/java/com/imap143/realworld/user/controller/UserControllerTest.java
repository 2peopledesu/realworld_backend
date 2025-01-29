package com.imap143.realworld.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imap143.realworld.exception.DuplicateEmailException;
import com.imap143.realworld.security.CustomUserDetails;
import com.imap143.realworld.security.SecurityConfig;
import com.imap143.realworld.security.jwt.JwtProvider;
import com.imap143.realworld.user.dto.UserLoginRequestDto;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.imap143.realworld.user.dto.UserUpdateRequestDto;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.model.UserUpdateRequest;
import com.imap143.realworld.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WithValidInput_ReturnsCreatedUser() throws Exception {
        
        long userId = 1L;
        UserSignUpRequestDto requestDto = new UserSignUpRequestDto("test@test.com", "testuser", "password");
        User mockUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(mockUser, "id", userId);
        String token = "mock.jwt.token";

        given(userService.register(any())).willReturn(mockUser);
        given(jwtProvider.createToken(userId)).willReturn(token);

        
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.token").value(token));
    }

    @Test
    void registerUser_WithInvalidInput_ReturnsBadRequest() throws Exception {

        UserSignUpRequestDto requestDto = new UserSignUpRequestDto("", "", "");

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCurrentUser_WithValidToken_ReturnsUserInfo() throws Exception {
        
        long userId = 1L;
        User mockUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(mockUser, "id", userId);
        String token = "mock.jwt.token";

        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "testuser");
        given(userService.findById(userId)).willReturn(Optional.of(mockUser));
        given(jwtProvider.createToken(userId)).willReturn(token);

        
        mockMvc.perform(get("/user")
                .with(user(userDetails)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.token").value(token));
    }

    @Test
    void loginUser_WithValidCredentials_ReturnsUserInfo() throws Exception {
        
        long userId = 1L;
        User mockUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(mockUser, "id", userId);
        String token = "mock.jwt.token";

        UserLoginRequestDto loginRequest = new UserLoginRequestDto("test@test.com", "password");
        given(userService.login("test@test.com", "password")).willReturn(Optional.of(mockUser));
        given(jwtProvider.createToken(userId)).willReturn(token);

        
        mockMvc.perform(post("/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.token").value(token));
    }

    @Test
    void updateUser_WithValidInput_ReturnsUpdatedUser() throws Exception {
        long userId = 1L;
        User mockUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(mockUser, "id", userId);
        String token = "mock.jwt.token";

        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto(
            "new@test.com", 
            "newuser", 
            "New bio", 
            "new-image-url",
            null
        );

        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "testuser");
        given(userService.update(eq(userId), any(UserUpdateRequest.class))).willReturn(mockUser);
        given(jwtProvider.createToken(userId)).willReturn(token);

        mockMvc.perform(put("/user")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value("test@test.com"))
                .andExpect(jsonPath("$.user.username").value("testuser"))
                .andExpect(jsonPath("$.user.token").value(token));
    }

    @Test
    void updateUser_WithDuplicateEmail_ReturnsConflict() throws Exception {
        long userId = 1L;
        UserUpdateRequestDto updateRequest = new UserUpdateRequestDto(
            "existing@test.com", 
            null, 
            null, 
            null,
            null
        );

        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "testuser");
        given(userService.update(eq(userId), any(UserUpdateRequest.class)))
                .willThrow(new DuplicateEmailException("Email is already in use"));

        mockMvc.perform(put("/user")
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
} 