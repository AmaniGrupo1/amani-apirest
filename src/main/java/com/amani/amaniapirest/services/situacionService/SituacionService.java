package com.amani.amaniapirest.services.situacionService;


import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.dto.situacion.SituacionRequest;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Servicio que implementa la lógica de negocio para Situacion.
 *
 * <p>Coordina las operaciones principales y gestiona las reglas de dominio.</p>
 *
 * Servicio principal que implementa la lógica de negocio de SituacionService.
 *
 * <p>Responsable de gestionar las reglas de dominio y validaciones correspondientes.</p>
 */
@Service
@RequiredArgsConstructor
public class SituacionService {

    private final SituacionRepository repository;

    // CREAR
    /**
     * Crea y persiste un nuevo registro en el sistema.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public SituacionDTO create(SituacionRequest request) {
        Situacion s = new Situacion();
        s.setNombre(request.getNombre());
        s.setCategoria(request.getCategoria());
        s.setDescripcion(request.getDescripcion());
        s.setActivo(request.getActivo() != null ? request.getActivo() : true);

        return toDTO(repository.save(s));
    }

    // ACTUALIZAR
    /**
     * Actualiza la información de un registro existente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public SituacionDTO update(Long id, SituacionRequest request) {
        Situacion s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Situación no encontrada"));

        s.setNombre(request.getNombre());
        s.setCategoria(request.getCategoria());
        s.setDescripcion(request.getDescripcion());
        s.setActivo(request.getActivo());

        return toDTO(repository.save(s));
    }

    // ELIMINAR (soft delete recomendado)
    /**
     * Elimina un registro del sistema.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public void delete(Long id) {
        Situacion s = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Situación no encontrada"));

        s.setActivo(false);
        repository.save(s);
    }

    // MAPPER
    private SituacionDTO toDTO(Situacion s) {
        return new SituacionDTO(
                s.getIdSituacion(),
                s.getNombre(),
                s.getCategoria(),
                s.getDescripcion()
        );
    }
}