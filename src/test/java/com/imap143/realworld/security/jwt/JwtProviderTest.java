package com.imap143.realworld.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;

    @Mock
    private UserDetailsService userDetailsService;

    private final UserDetails userDetails = new User("test@test.com", "", new ArrayList<>());

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        ReflectionTestUtils.setField(jwtProperties, "secretKey", "testSecretKeytestSecretKeytestSecretKeytestSecretKey");
        ReflectionTestUtils.setField(jwtProperties, "tokenValidityInSeconds", 3600L);

        jwtProvider = new JwtProvider(jwtProperties, userDetailsService);
        jwtProvider.init();
    }

    @Test
    void createToken_WithValidEmail_ReturnsValidToken() {
        // given
        String email = "test@test.com";

        // when
        String token = jwtProvider.createToken(email);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtProvider.validateToken(token)).isTrue();
        assertThat(jwtProvider.getEmail(token)).isEqualTo(email);
    }

    @Test
    void validateToken_WithInvalidToken_ReturnsFalse() {
        // given
        String invalidToken = "invalid.token.here";

        // when & then
        assertThat(jwtProvider.validateToken(invalidToken)).isFalse();
    }

    @Test
    void getAuthentication_WithValidToken_ReturnsAuthentication() {
        // given
        String email = "test@test.com";
        String token = jwtProvider.createToken(email);
        given(userDetailsService.loadUserByUsername(email)).willReturn(userDetails);

        // when
        var authentication = jwtProvider.getAuthentication(token);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(email);
    }
} 