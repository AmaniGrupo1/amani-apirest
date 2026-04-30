package com.amani.amaniapirest.configuration;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private static final String TEST_SECRET = "12345678901234567890123456789012";

    @Test
    void generateTokenIncludesSubjectRoleAndValidatesMatchingUser() {
        JwtUtil jwtUtil = jwtUtilWithExpiration(60_000L);
        UserDetails userDetails = User.withUsername("ana@amani.com")
                .password("encoded-password")
                .authorities(List.of())
                .build();

        String token = jwtUtil.generateToken(userDetails, "paciente");

        assertThat(jwtUtil.extractEmail(token)).isEqualTo("ana@amani.com");
        assertThat(jwtUtil.extractRol(token)).isEqualTo("paciente");
        assertThat(jwtUtil.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void tokenIsInvalidForDifferentUser() {
        JwtUtil jwtUtil = jwtUtilWithExpiration(60_000L);
        UserDetails owner = User.withUsername("ana@amani.com")
                .password("encoded-password")
                .authorities(List.of())
                .build();
        UserDetails otherUser = User.withUsername("otro@amani.com")
                .password("encoded-password")
                .authorities(List.of())
                .build();

        String token = jwtUtil.generateToken(owner, "paciente");

        assertThat(jwtUtil.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void expiredTokenIsRejected() {
        JwtUtil jwtUtil = jwtUtilWithExpiration(-1_000L);
        UserDetails userDetails = User.withUsername("ana@amani.com")
                .password("encoded-password")
                .authorities(List.of())
                .build();

        String token = jwtUtil.generateToken(userDetails, "paciente");

        assertThatThrownBy(() -> jwtUtil.isTokenValid(token, userDetails))
                .isInstanceOf(ExpiredJwtException.class);
    }

    private JwtUtil jwtUtilWithExpiration(long expiration) {
        JwtUtil jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", expiration);
        return jwtUtil;
    }
}
