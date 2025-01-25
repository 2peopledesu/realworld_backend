package com.imap143.realworld.user.controller;

import com.imap143.realworld.security.jwt.JwtProvider;
import com.imap143.realworld.user.dto.UserResponseDto;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.imap143.realworld.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        final var savedUser = userService.register(requestDto.signUpRequest());
        String token = jwtProvider.createToken(savedUser.getEmail());

        return ResponseEntity.ok(UserResponseDto.fromUser(savedUser, token));
    }

}