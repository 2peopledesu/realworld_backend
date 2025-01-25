package com.imap143.realworld.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imap143.realworld.security.SecurityConfig;
import com.imap143.realworld.security.jwt.JwtProvider;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        UserSignUpRequestDto requestDto = new UserSignUpRequestDto("test@test.com", "testuser", "password");
        User mockUser = User.of("test@test.com", "testuser", null);
        String token = "mock.jwt.token";

        given(userService.register(any())).willReturn(mockUser);
        given(jwtProvider.createToken(any())).willReturn(token);

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
} 