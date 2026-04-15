package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.HorarioPsicologo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para operaciones de persistencia sobre la entidad {@link HorarioPsicologo}.
 */
public interface HorarioPsicologoRepository extends JpaRepository<HorarioPsicologo, Long> {
    List<HorarioPsicologo> findByPsicologoIdPsicologoAndActivoTrue(Long idPsicologo);

    @Modifying
    @Query("DELETE FROM HorarioPsicologo h WHERE h.psicologo.idPsicologo = :idPsicologo")
    void deleteByPsicologoIdPsicologo(@Param("idPsicologo") Long idPsicologo);
}