package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public LoginResponseDTO registerPaciente(@RequestBody PacienteRequestDTO request){
        return authService.registerPaciente(request);
    }


    //Listo los administradores
    @GetMapping("/admins")
    public ResponseEntity<List<AdministradorDTO>> listarAdmins() {
        List<AdministradorDTO> user = authService.listarAdministradores();
        if (user.isEmpty()){
            ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register-admin")
    public LoginResponseDTO registerAdmin(@RequestBody RegistryRequestDTO request){
        return authService.registerAdmin(request);
    }

   /** @PostMapping("/register-psicologo")
    public LoginResponseDTO registerPsicologo(@RequestBody RegistryRequestDTO request){
        return authService.registerPsicologo(request);
    } **/

    @PutMapping("/pacientes/{id}/baja")
    public ResponseEntity<String> darBajaPaciente(@PathVariable Long id) {
        authService.darBajaPaciente(id);
        return ResponseEntity.ok("Paciente dado de baja correctamente");
    }
}