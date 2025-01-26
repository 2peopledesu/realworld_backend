package com.imap143.realworld.security;

import com.imap143.realworld.user.model.User;
import com.imap143.realworld.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_WithValidUserId_ReturnsUserDetails() {
        long userId = 1L;
        User user = User.of("test@test.com", "testuser", null);
        ReflectionTestUtils.setField(user, "id", userId);
        
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(String.valueOf(userId));

        assertThat(userDetails).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertThat(customUserDetails.getId()).isEqualTo(userId);
        assertThat(customUserDetails.getEmail()).isEqualTo("test@test.com");
        assertThat(customUserDetails.getRealUsername()).isEqualTo("testuser");
    }

    @Test
    void loadUserByUsername_WithInvalidUserId_ThrowsException() {
        long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        String userIdString = String.valueOf(userId);
        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(userIdString));
    }
} 