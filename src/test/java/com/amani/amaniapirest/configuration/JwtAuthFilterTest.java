package com.amani.amaniapirest.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    private JwtAuthFilter jwtAuthFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(jwtUtil, userDetailsService);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenValido_setAuthenticationEnSecurityContext() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtil.extractEmail("validToken")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_PACIENTE"))).when(userDetails).getAuthorities();
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("validToken", userDetails)).thenReturn(true);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("user@example.com");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenExpirado_noAutentica_continuaCadena() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");
        when(jwtUtil.extractEmail("expiredToken")).thenThrow(new RuntimeException("Token expirado"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenMalformado_noAutentica() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn("Basic token");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void tokenConFirmaInvalida_noAutentica() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtil.extractEmail("invalidToken")).thenReturn("user@example.com");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("invalidToken", userDetails)).thenReturn(false);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void headerAuthorizationAusente_noAutentica() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void rutaPublica_saltaFiltro() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/auth/login");

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void userDetailsServiceLanzaExcepcion_noAutentica_continuaCadena() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/api/situaciones");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(jwtUtil.extractEmail("validToken")).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenThrow(new RuntimeException("Usuario bloqueado"));

        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
