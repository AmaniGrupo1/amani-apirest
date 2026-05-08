package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.gateway.FirebaseTokenGateway;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FirebaseAuthControllerTest {

    @Mock
    private FirebaseTokenGateway firebaseTokenGateway;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private FirebaseAuthController firebaseAuthController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(firebaseAuthController)
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterAnnotation(AuthenticationPrincipal.class) != null
                                && UserDetails.class.isAssignableFrom(parameter.getParameterType());
                    }

                    @Override
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                        return userDetails;
                    }
                })
                .build();
    }

    @Test
    @DisplayName("getFirebaseToken 200 con token válido")
    void getFirebaseToken200() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setEmail("test@amani.com");
        usuario.setRol(RolUsuario.paciente);

        when(userDetails.getUsername()).thenReturn("test@amani.com");
        when(usuarioRepository.findByEmail("test@amani.com")).thenReturn(Optional.of(usuario));
        when(firebaseTokenGateway.createCustomToken(eq("1"), anyMap())).thenReturn("mock-firebase-token");

        mockMvc.perform(get("/api/auth/firebase-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firebaseToken").value("mock-firebase-token"));
    }

    @Test
    @DisplayName("getFirebaseToken 401 cuando usuario no existe")
    void getFirebaseToken401() throws Exception {
        when(userDetails.getUsername()).thenReturn("unknown@amani.com");
        when(usuarioRepository.findByEmail("unknown@amani.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/auth/firebase-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("getFirebaseToken 503 cuando Firebase no disponible")
    void getFirebaseToken503() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(2L);
        usuario.setEmail("test@amani.com");
        usuario.setRol(RolUsuario.psicologo);

        when(userDetails.getUsername()).thenReturn("test@amani.com");
        when(usuarioRepository.findByEmail("test@amani.com")).thenReturn(Optional.of(usuario));
        when(firebaseTokenGateway.createCustomToken(eq("2"), anyMap()))
                .thenThrow(new UnsupportedOperationException("Firebase no disponible"));

        mockMvc.perform(get("/api/auth/firebase-token"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Firebase Auth no está disponible en este entorno."));
    }

    @Test
    @DisplayName("getFirebaseToken 500 cuando error inesperado")
    void getFirebaseToken500() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setEmail("test@amani.com");
        usuario.setRol(RolUsuario.admin);

        when(userDetails.getUsername()).thenReturn("test@amani.com");
        when(usuarioRepository.findByEmail("test@amani.com")).thenReturn(Optional.of(usuario));
        when(firebaseTokenGateway.createCustomToken(eq("3"), anyMap()))
                .thenThrow(new RuntimeException("Error inesperado"));

        mockMvc.perform(get("/api/auth/firebase-token"))
                .andExpect(status().isInternalServerError());
    }
}
