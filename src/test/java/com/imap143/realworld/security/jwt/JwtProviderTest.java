package com.imap143.realworld.security.jwt;

import com.imap143.realworld.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        ReflectionTestUtils.setField(jwtProperties, "secretKey", "testsecretkeytestsecretkeytestsecretkey");
        ReflectionTestUtils.setField(jwtProperties, "tokenValidityInSeconds", 3600);

        jwtProvider = new JwtProvider(jwtProperties, userDetailsService);
        jwtProvider.init();
    }

    @Test
    void createToken_WithUserId_ReturnsValidToken() {

        long userId = 1L;

        String token = jwtProvider.createToken(userId);

        assertThat(token).isNotNull();
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getUserId(token)).isEqualTo(String.valueOf(userId));
    }

    @Test
    void validateToken_WithInvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    void getAuthentication_WithValidToken_ReturnsAuthentication() {
        long userId = 1L;
        String token = jwtProvider.createToken(userId);
        CustomUserDetails userDetails = new CustomUserDetails(userId, "test@test.com", "testuser");
        given(userDetailsService.loadUserByUsername(String.valueOf(userId))).willReturn(userDetails);

        Authentication authentication = jwtProvider.getAuthentication(token);

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        assertThat(principal.getId()).isEqualTo(userId);
    }
} 