package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    List<Tutor> findByPacienteIdPaciente(Long idPaciente);
}
