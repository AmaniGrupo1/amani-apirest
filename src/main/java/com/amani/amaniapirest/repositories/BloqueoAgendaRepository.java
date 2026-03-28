package com.amani.amaniapirest.repositories;

import com.amani.amaniapirest.models.BloqueoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BloqueoAgendaRepository extends JpaRepository<BloqueoAgenda, Long> {
    List<BloqueoAgenda> findByPsicologoIdPsicologoAndFecha(Long idPsicologo, LocalDate fecha);

    List<BloqueoAgenda> findByPsicologoIdPsicologoAndFechaBetween(
            Long idPsicologo, LocalDate inicio, LocalDate fin
    );

    Optional<BloqueoAgenda> findByPsicologoIdPsicologoAndFechaAndHoraInicioIsNull(
            Long idPsicologo, LocalDate fecha
    );

    void deleteByPsicologoIdPsicologoAndFecha(Long idPsicologo, LocalDate fecha);
}