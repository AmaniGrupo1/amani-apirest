package com.amani.amaniapirest.services.terapiaService;

import com.amani.amaniapirest.dto.terapiasDTO.TerapiaRequestDTO;
import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
import com.amani.amaniapirest.models.TiposTerapia;
import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void eliminarTerapia(Long id) {

        TiposTerapia terapia = terapiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Terapia no encontrada"));

        terapia.setActivo(false);

        terapiaRepository.save(terapia);
    }
}
