package com.amani.amaniapirest.services.terapiaService;

import com.amani.amaniapirest.dto.terapiasDTO.TerapiaRequestDTO;
import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
import com.amani.amaniapirest.models.TiposTerapia;
import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que implementa la lógica de negocio para Terapia.
 *
 * <p>Coordina las operaciones principales y gestiona las reglas de dominio.</p>
 *
 * Servicio principal que implementa la lógica de negocio de TerapiaService.
 *
 * <p>Responsable de gestionar las reglas de dominio y validaciones correspondientes.</p>
 */
@Service
@RequiredArgsConstructor
public class TerapiaService {

    private final TerapiaRepository terapiaRepository;

    public List<TerapiaResponseDTO> getAllTerapias() {
        return terapiaRepository.findByActivoTrue()
                .stream()
                .map(t -> new TerapiaResponseDTO(
                        t.getIdTipo(),
                        t.getNombre(),
                        t.getDuracionMinutos(),
                        t.getPrecio()
                ))
                .toList();
    }


    /**
     * Ejecuta la operación correspondiente a crearTerapia.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public TerapiaResponseDTO crearTerapia(TerapiaRequestDTO request) {

        TiposTerapia terapia = new TiposTerapia();
        terapia.setNombre(request.getNombre());
        terapia.setDuracionMinutos(request.getDuracionMinutos());
        terapia.setPrecio(request.getPrecio());
        terapia.setActivo(true);

        TiposTerapia guardada = terapiaRepository.save(terapia);

        return new TerapiaResponseDTO(
                guardada.getIdTipo(),
                guardada.getNombre(),
                guardada.getDuracionMinutos(),
                guardada.getPrecio()
        );
    }


    /**
     * Actualiza la información de un registro existente.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public TerapiaResponseDTO actualizarTerapia(Long id, TerapiaRequestDTO request) {

        TiposTerapia terapia = terapiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia no encontrada"));

        terapia.setNombre(request.getNombre());
        terapia.setDuracionMinutos(request.getDuracionMinutos());
        terapia.setPrecio(request.getPrecio());

        TiposTerapia actualizada = terapiaRepository.save(terapia);

        return new TerapiaResponseDTO(
                actualizada.getIdTipo(),
                actualizada.getNombre(),
                actualizada.getDuracionMinutos(),
                actualizada.getPrecio()
        );
    }

    /**
     * Elimina un registro del sistema.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public void eliminarTerapia(Long id) {

        TiposTerapia terapia = terapiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia no encontrada"));

        terapia.setActivo(false);

        terapiaRepository.save(terapia);
    }
}
