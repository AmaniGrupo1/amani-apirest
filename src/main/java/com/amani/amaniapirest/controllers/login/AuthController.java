package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** LOGIN */
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    @PostMapping("/register-paciente")
    public LoginResponseDTO registerPaciente(@RequestBody RegistryRequestDTO request){
        return authService.registerPaciente(request);
    }

    @PostMapping("/register-admin")
    public LoginResponseDTO registerAdmin(@RequestBody RegistryRequestDTO request){
        return authService.registerAdmin(request);
    }

    @PostMapping("/register-psicologo")
    public LoginResponseDTO registerPsicologo(@RequestBody RegistryRequestDTO request){
        return authService.registerPsicologo(request);
    }
}