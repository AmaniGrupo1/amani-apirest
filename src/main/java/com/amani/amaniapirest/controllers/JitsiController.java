package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.enums.RolUsuario;
import com.amani.amaniapirest.repository.UsuarioRepository;
import com.amani.amaniapirest.services.JitsiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/jitsi")
@Tag(name = "Jitsi Controller", description = "Endpoints para videollamadas con Jitsi")
public class JitsiController {

    @Autowired
    private JitsiService jitsiService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(summary = "Obtener Token Jitsi", description = "Genera un token JWT para entrar en una sala de Jitsi con los permisos correspondientes")
    @GetMapping("/token/{room}")
    public ResponseEntity<?> getJitsiToken(@PathVariable String room) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Usuario no autenticado"));
        }

        Usuario usuario = usuarioOpt.get();
        // Solo administradores o psicólogos son moderadores en la videollamada
        boolean isModerator = usuario.getRol() == RolUsuario.psicologo || usuario.getRol() == RolUsuario.admin;

        String token = jitsiService.generateToken(
                room,
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getEmail(),
                usuario.getFotoPerfilUrl(),
                isModerator
        );

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
}
