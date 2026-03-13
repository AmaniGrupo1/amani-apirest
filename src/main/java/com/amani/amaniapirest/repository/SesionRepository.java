package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SesionRepository extends JpaRepository<Sesion, Long> {

    List<Sesion> findByPaciente_IdPaciente(Long idPaciente);
    List<Sesion> findByPsicologo_IdPsicologo(Long idPsicologo);
}
