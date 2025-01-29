package com.imap143.realworld.user.repository;

import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void findByEmail_WithExistingEmail_ReturnsUser() {

        Password password = Password.of("password", passwordEncoder);
        User user = User.of("test@test.com", "testuser", password);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@test.com");
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsername_WithExistingUsername_ReturnsUser() {

        Password password = Password.of("password", passwordEncoder);
        User user = User.of("test@test.com", "testuser", password);
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("test@test.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }
} 