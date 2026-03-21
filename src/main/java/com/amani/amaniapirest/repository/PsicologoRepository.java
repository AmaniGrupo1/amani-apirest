package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Psicologo;
import com.amani.amaniapirest.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PsicologoRepository extends JpaRepository<Psicologo, Long> {
    /** Comprueba si ya existe un psicólogo asociado a este usuario */
    boolean existsByUsuario(Usuario usuario);
}