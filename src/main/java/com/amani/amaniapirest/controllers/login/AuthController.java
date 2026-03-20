package com.amani.amaniapirest.controllers.login;

import com.amani.amaniapirest.dto.dtoPaciente.request.PacienteRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginRequestDTO;
import com.amani.amaniapirest.dto.loginDTO.LoginResponseDTO;
import com.amani.amaniapirest.dto.loginDTO.RegistryRequestDTO;
import com.amani.amaniapirest.services.serviciosLogin.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Controlador REST de autenticacion y registro de usuarios.
 *
 * <p>Base path: {@code /auth}. Expone endpoints publicos para login y registro
 * de pacientes, y endpoints protegidos para registro de admin y psicologo.</p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Login, registro de pacientes, admins y psicologos")
public class AuthController {

    private final AuthService authService;

    /**
     * Autentica un usuario y devuelve un token JWT.
     *
     * @param request credenciales (email y password).
     * @return datos del usuario autenticado con su token JWT.
     */
    @Operation(summary = "Login", description = "Autentica un usuario y devuelve un token JWT",
               security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

    /**
     * Registra un nuevo paciente (endpoint publico).
     *
     * @param request datos del paciente incluyendo su usuario.
     * @return datos del paciente registrado con su token JWT.
     */
    @Operation(summary = "Registro de paciente", description = "Registra un nuevo paciente (publico, sin token)",
               security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/register-paciente")
    public LoginResponseDTO registerPaciente(@RequestBody PacienteRequestDTO request){
        return authService.registerPaciente(request);
    }

    /**
     * Registra un paciente desde el panel de administracion.
     *
     * @param request datos del paciente incluyendo su usuario.
     * @return datos del paciente registrado con su token JWT.
     */
    @Operation(summary = "Registro de paciente (admin)", description = "Registra un paciente desde el panel de administracion")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/registry/pacienteAdmin")
    public LoginResponseDTO registrarPacienteAdmin(@RequestBody PacienteRequestDTO request){
        return authService.registerPaciente(request);
    }

    /**
     * Registra un nuevo administrador (requiere rol admin).
     *
     * @param request datos del nuevo admin.
     * @return datos del admin registrado con su token JWT.
     */
    @Operation(summary = "Registro de admin", description = "Registra un nuevo administrador (requiere rol admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/register-admin")
    public LoginResponseDTO registerAdmin(@RequestBody RegistryRequestDTO request){
        return authService.registerAdmin(request);
    }

    /**
     * Registra un nuevo psicologo (requiere rol admin).
     *
     * @param request datos del nuevo psicologo.
     * @return datos del psicologo registrado con su token JWT.
     */
    @Operation(summary = "Registro de psicologo", description = "Registra un nuevo psicologo (requiere rol admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/register-psicologo")
    public LoginResponseDTO registerPsicologo(@RequestBody RegistryRequestDTO request){
        return authService.registerPsicologo(request);
    }

    /**
     * Da de baja a un paciente desactivando su cuenta de usuario.
     *
     * @param id identificador del usuario del paciente.
     * @return mensaje de confirmacion.
     */
    @Operation(summary = "Baja de paciente", description = "Desactiva la cuenta de un paciente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación realizada correctamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado — token JWT ausente o inválido", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PutMapping("/pacientes/{id}/baja")
    public ResponseEntity<String> darBajaPaciente(@PathVariable Long id) {
        authService.darBajaPaciente(id);
        return ResponseEntity.ok("Paciente dado de baja correctamente");
    }
}