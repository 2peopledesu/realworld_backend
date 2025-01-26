package com.imap143.realworld.user.controller;

import com.imap143.realworld.security.jwt.JwtProvider;
import com.imap143.realworld.user.dto.UserLoginRequestDto;
import com.imap143.realworld.user.dto.UserResponseDto;
import com.imap143.realworld.user.dto.UserSignUpRequestDto;
import com.imap143.realworld.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.ResponseEntity.of;
import com.imap143.realworld.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;

    @PostMapping(value = "/users")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserSignUpRequestDto requestDto) {
        final var savedUser = userService.register(requestDto.signUpRequest());
        String token = jwtProvider.createToken(savedUser.getId());
        return ResponseEntity.ok(UserResponseDto.fromUser(savedUser, token));
    }

    @PostMapping(value = "/users/login")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody UserLoginRequestDto requestDto) {
        return of(userService.login(requestDto.getEmail(), requestDto.getPassword())
                .map(user -> {
                    String token = jwtProvider.createToken(user.getId());
                    return UserResponseDto.fromUser(user, token);
                }));
    }

    @GetMapping(value = "/user")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return of(userService.findById(userDetails.getId())
                .map(currentUser -> {
                    String token = jwtProvider.createToken(currentUser.getId());
                    return UserResponseDto.fromUser(currentUser, token);
                }));
    }
}