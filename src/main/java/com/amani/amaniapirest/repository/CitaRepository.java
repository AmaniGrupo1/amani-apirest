package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO;
import com.amani.amaniapirest.enums.EstadoCita;
import com.amani.amaniapirest.models.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface CitaRepository extends JpaRepository<Cita, Long> {

    List<Cita> findByPsicologo_IdPsicologo(Long idPsicologo);

    List<Cita> findByPaciente_IdPaciente(Long idPaciente);

    @Query("""
                SELECT new com.amani.amaniapirest.dto.dtoAdmin.response.PsicologoAdminResponseDTO(
                    psi.usuario.nombre, psi.usuario.apellido,
                    pac.usuario.nombre, pac.usuario.apellido,
                    pac.usuario.email, pac.usuario.fechaRegistro
                )
                FROM Cita c
                JOIN c.psicologo psi
                JOIN c.paciente pac
            """)
    List<PsicologoAdminResponseDTO> getPacientesConPsicologo();

    List<Cita> findByStartDatetimeBetweenAndEstado(LocalDateTime desde, LocalDateTime hasta, EstadoCita name);
}

