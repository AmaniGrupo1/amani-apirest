package com.amani.amaniapirest.services.terapiaService;

import com.amani.amaniapirest.dto.terapiasDTO.TerapiaResponseDTO;
import com.amani.amaniapirest.repository.terapiaService.TerapiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TerapiaService {

    private final TerapiaRepository terapiaRepository;

    public List<TerapiaResponseDTO> getAllTerapias() {
        return terapiaRepository.findAll()
                .stream()
                .map(t -> new TerapiaResponseDTO(
                        t.getIdTipo(),
                        t.getNombre(),
                        t.getDuracionMinutos()
                ))
                .toList();
    }
}
