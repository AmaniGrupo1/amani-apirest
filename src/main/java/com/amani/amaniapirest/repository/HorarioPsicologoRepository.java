package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.HorarioPsicologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioPsicologoRepository extends JpaRepository<HorarioPsicologo, Long> {
    List<HorarioPsicologo> findByPsicologoIdPsicologoAndActivoTrue(Long idPsicologo);

    void deleteByPsicologoIdPsicologo(Long idPsicologo);
}