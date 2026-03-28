package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.PacienteSituacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteSituacionRepository extends JpaRepository<PacienteSituacion, Long> {
    void deleteAllByPaciente_IdPaciente(Long idPaciente);
}
