package com.amani.amaniapirest.controllers.security;

import com.amani.amaniapirest.configuration.JwtAuthFilter;
import com.amani.amaniapirest.configuration.JwtUtil;
import com.amani.amaniapirest.configuration.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityAccessTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void cleanContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void publicRouteSkipsTokenValidation() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/auth/login");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        verify(jwtUtil, never()).extractEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void requestWithoutBearerKeepsUnauthenticatedContext() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/preguntas");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).extractEmail(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void validBearerTokenAuthenticatesUserInContext() throws ServletException, IOException {
        JwtAuthFilter filter = new JwtAuthFilter(jwtUtil, userDetailsService);

        UserDetails userDetails = User.withUsername("admin@amani.com")
                .password("noop")
                .authorities("ROLE_ADMIN")
                .build();

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/preguntas");
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtUtil.extractEmail("valid-token")).thenReturn("admin@amani.com");
        when(userDetailsService.loadUserByUsername("admin@amani.com")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("valid-token", userDetails)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil).extractEmail("valid-token");
        verify(userDetailsService).loadUserByUsername("admin@amani.com");
    }
}
