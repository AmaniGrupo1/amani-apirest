package com.amani.amaniapirest.services.situacionService;


import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.dto.situacion.SituacionRequest;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SituacionService {

    private final SituacionRepository repository;

    // CREAR
    public SituacionDTO create(SituacionRequest request) {
        Situacion s = new Situacion();
        s.setNombre(request.getNombre());
        s.setCategoria(request.getCategoria());
        s.setDescripcion(request.getDescripcion());
        s.setActivo(request.getActivo() != null ? request.getActivo() : true);

        return toDTO(repository.save(s));
    }

    // ACTUALIZAR
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