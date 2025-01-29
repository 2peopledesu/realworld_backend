package com.imap143.realworld.user.service;

import com.imap143.realworld.exception.DuplicateEmailException;
import com.imap143.realworld.exception.DuplicateUsernameException;
import com.imap143.realworld.user.model.Password;
import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.model.UserSignUpRequest;
import com.imap143.realworld.user.model.UserUpdateRequest;
import com.imap143.realworld.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;

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

    @Test
    void update_WithValidInput_ReturnsUpdatedUser() {
    
        long userId = 1L;
        User existingUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(existingUser, "id", userId);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("new@test.com")
                .bio("New bio")
                .image("new-image-url")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail("new@test.com")).willReturn(Optional.empty());

        User updatedUser = userService.update(userId, updateRequest);

        assertThat(updatedUser.getEmail()).isEqualTo("new@test.com");
        assertThat(updatedUser.getBio()).isEqualTo("New bio");
        assertThat(updatedUser.getProfile().getImage()).isEqualTo("new-image-url");
    }

    @Test
    void update_WithDuplicateEmail_ThrowsException() {

        long userId = 1L;
        User existingUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(existingUser, "id", userId);

        User anotherUser = User.of("existing@test.com", "another", null);
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .email("existing@test.com")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByEmail("existing@test.com")).willReturn(Optional.of(anotherUser));

        assertThrows(DuplicateEmailException.class, () ->
            userService.update(userId, updateRequest));
    }

    @Test
    void update_WithDuplicateUsername_ThrowsException() {

        long userId = 1L;
        User existingUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(existingUser, "id", userId);

        User anotherUser = User.of("another@test.com", "existingname", null);
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder()
                .username("existingname")
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));
        given(userRepository.findByProfile_Username("existingname")).willReturn(Optional.of(anotherUser));

        assertThrows(DuplicateUsernameException.class, () ->
            userService.update(userId, updateRequest));
    }

    @Test
    void update_WithNoChanges_ReturnsOriginalUser() {

        long userId = 1L;
        User existingUser = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(existingUser, "id", userId);

        UserUpdateRequest updateRequest = UserUpdateRequest.builder().build();

        given(userRepository.findById(userId)).willReturn(Optional.of(existingUser));

        User updatedUser = userService.update(userId, updateRequest);

        assertThat(updatedUser).isEqualTo(existingUser);
        assertThat(updatedUser.getEmail()).isEqualTo("test@test.com");
        assertThat(updatedUser.getUsername()).isEqualTo("testuser");
    }
} 