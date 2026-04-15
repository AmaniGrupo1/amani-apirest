package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.dto.dtoAdmin.response.AdministradorDTO;
import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la autenticación de usuarios y registro de nuevos usuarios.
 *
 * <p>Expone endpoints para login, registro de pacientes y administradores, así como
 * la baja de pacientes del sistema.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Operaciones de autenticación y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    /**
     * Inicia sesión con las credenciales de un usuario.
     *
     * @param request {@link LoginRequestDTO} con email y contraseña
     * @return {@link LoginResponseDTO} con el token JWT y datos del usuario
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param request {@link PacienteRequestDTO} con los datos del paciente
     * @return {@link LoginResponseDTO} con el token JWT y datos del usuario
     */
    @Operation(summary = "Registrar paciente", description = "Crea una nueva cuenta de paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro exitoso"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/register-paciente")
    public LoginResponseDTO registerPaciente(@RequestBody PacienteRequestDTO request){
        return authService.registerPaciente(request);
    }


    //Listo los administradores
    /**
     * Lista todos los administradores del sistema.
     *
     * @return lista de {@link AdministradorDTO}
     */
    @Operation(summary = "Listar administradores", description = "Obtiene la lista completa de administradores")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @GetMapping("/admins")
    public ResponseEntity<List<AdministradorDTO>> listarAdmins() {
        List<AdministradorDTO> user = authService.listarAdministradores();
        if (user.isEmpty()){
            ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Registra un nuevo administrador en el sistema.
     *
     * @param request {@link RegistryRequestDTO} con los datos del administrador
     * @return {@link LoginResponseDTO} con el token JWT
     */
    @Operation(summary = "Registrar administrador", description = "Crea una nueva cuenta de administrador")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro exitoso"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/register-admin")
    public LoginResponseDTO registerAdmin(@RequestBody RegistryRequestDTO request){
        return authService.registerAdmin(request);
    }

    /**
     * Da de baja a un paciente del sistema.
     *
     * @param id identificador del paciente a dar de baja
     * @return mensaje de confirmación
     */
    @Operation(summary = "Dar de baja paciente", description = "Desactiva la cuenta de un paciente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Paciente dado de baja exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/pacientes/{id}/baja")
    public ResponseEntity<String> darBajaPaciente(@PathVariable Long id) {
        authService.darBajaPaciente(id);
        return ResponseEntity.ok("Paciente dado de baja correctamente");
    }
}