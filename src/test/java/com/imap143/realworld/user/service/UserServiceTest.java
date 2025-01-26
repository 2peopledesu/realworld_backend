package com.imap143.realworld.user.service;

import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.model.UserSignUpRequest;
import com.imap143.realworld.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_WithValidInput_ReturnsUser() {

        UserSignUpRequest request = new UserSignUpRequest("test@test.com", "testuser", "password");
        User expectedUser = User.of("test@test.com", "testuser", null);
        
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(expectedUser);

        User actualUser = userService.register(request);

        assertThat(actualUser.getEmail()).isEqualTo("test@test.com");
        assertThat(actualUser.getUsername()).isEqualTo("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_WithValidInput_ReturnsUser() {

        User user = User.of("test@test.com", "testuser", Password.of("password", passwordEncoder));

        final var PasswordEncoder = passwordEncoder;
        given(userRepository.findByEmail("test@test.com")).willReturn(java.util.Optional.of(user));
        given(user.matchPassword("password", PasswordEncoder)).willReturn(true);

        java.util.Optional<User> actualUser = userService.login("test@test.com", "password");

        assertThat(actualUser).isPresent();
        assertThat(actualUser.get().getEmail()).isEqualTo("test@test.com");
        assertThat(actualUser.get().getUsername()).isEqualTo("testuser");
    }
    
} 