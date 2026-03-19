package com.amani.amaniapirest.repository.repositoryRespuesta;

import com.amani.amaniapirest.dto.dtoPregunta.psicologo.RespuestaPacientePsicologoResponseDTO;
import com.amani.amaniapirest.models.modelPreguntasInicial.Respuesta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    Respuesta findByPaciente_IdPaciente(Long idPaciente);

    @Query("""
                SELECT RespuestaPacientePsicologoResponseDTO(
                    CONCAT(pac.usuario.nombre,' ',pac.usuario.apellido),
                    pre.texto,
                    r.texto,
                    op.texto,
                    r.creadoEn
                )
                FROM Respuesta r
                JOIN r.paciente pac
                JOIN r.pregunta pre
                LEFT JOIN r.opcion op
                WHERE pac.idPaciente = :idPaciente
            """)
    List<RespuestaPacientePsicologoResponseDTO> obtenerRespuestasPaciente(@Param("idPaciente") Long idPaciente);

    @Query("""
        SELECT r
        FROM Respuesta r
        JOIN FETCH r.pregunta
        LEFT JOIN FETCH r.opcion
        JOIN FETCH r.paciente p
        JOIN FETCH p.usuario
    """)
    List<Respuesta> findAllWithPaciente();
}
