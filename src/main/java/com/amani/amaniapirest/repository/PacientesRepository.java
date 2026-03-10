package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacientesRepository extends JpaRepository<Paciente, Long>{

}