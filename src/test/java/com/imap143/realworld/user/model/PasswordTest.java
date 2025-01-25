package com.imap143.realworld.user.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void of_WithValidPassword_CreatesEncodedPassword() {

        String rawPassword = "password123";

        Password password = Password.of(rawPassword, passwordEncoder);

        assertThat(password).isNotNull();
    }
} 