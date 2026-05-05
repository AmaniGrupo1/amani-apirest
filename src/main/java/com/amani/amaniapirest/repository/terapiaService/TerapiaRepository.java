package com.amani.amaniapirest.repository.terapiaService;

import com.amani.amaniapirest.models.TiposTerapia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TerapiaRepository extends JpaRepository<TiposTerapia, Long> {
    List<TiposTerapia> findByActivoTrue();
}
