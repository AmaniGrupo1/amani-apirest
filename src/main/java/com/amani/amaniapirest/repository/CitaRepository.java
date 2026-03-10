package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Cita;
import com.amani.amaniapirest.models.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CitaRepository extends JpaRepository<Cita, Long>{

}