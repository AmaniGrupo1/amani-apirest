package com.amani.amaniapirest.controllers;

import com.amani.amaniapirest.dto.dtoPaciente.request.UsuarioRequestDTO;
import com.amani.amaniapirest.dto.dtoPaciente.response.UsuarioResponseDTO;
import com.amani.amaniapirest.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios del sistema.
 *
 * <p>Expone los endpoints CRUD sobre {@link com.amani.amaniapirest.models.Usuario}
 * delegando la lógica de negocio a {@link UsuarioService}.</p>
 *
 * <p>Base URL: {@code /api/usuarios}</p>
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    /**
     * Construye el controlador inyectando el servicio de usuarios.
     *
     * @param usuarioService servicio de negocio para la gestión de usuarios
     */
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene la lista de todos los usuarios registrados.
     *
     * <p>GET /api/usuarios</p>
     *
     * @return 200 OK con la lista de {@link UsuarioResponseDTO}
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    /**
     * Obtiene un usuario por su identificador.
     *
     * <p>GET /api/usuarios/{id}</p>
     *
     * @param id identificador del usuario
     * @return 200 OK con el {@link UsuarioResponseDTO} encontrado,
     *         o 404 Not Found si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Crea un nuevo usuario.
     *
     * <p>POST /api/usuarios</p>
     *
     * @param request cuerpo de la petición con los datos del nuevo usuario
     * @return 201 Created con el {@link UsuarioResponseDTO} del usuario creado
     */
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(@Valid @RequestBody UsuarioRequestDTO request) {
        UsuarioResponseDTO created = usuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Actualiza los datos de un usuario existente.
     *
     * <p>PUT /api/usuarios/{id}</p>
     *
     * @param id      identificador del usuario a actualizar
     * @param request cuerpo de la petición con los nuevos datos
     * @return 200 OK con el {@link UsuarioResponseDTO} actualizado,
     *         o 404 Not Found si el usuario no existe
     */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO request) {
        try {
            return ResponseEntity.ok(usuarioService.update(id, request));
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Elimina un usuario por su identificador.
     *
     * <p>DELETE /api/usuarios/{id}</p>
     *
     * @param id identificador del usuario a eliminar
     * @return 204 No Content si se eliminó correctamente,
     *         o 404 Not Found si el usuario no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            usuarioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}

