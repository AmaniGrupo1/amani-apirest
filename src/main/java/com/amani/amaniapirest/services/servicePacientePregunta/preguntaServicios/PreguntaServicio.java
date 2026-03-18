package com.amani.amaniapirest.services.servicePacientePregunta.preguntaServicios;

import com.amani.amaniapirest.dto.dtoPregunta.admin.OpcionAdminResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.paciente.PreguntaPacienteResponseDTO;
import com.amani.amaniapirest.dto.dtoPregunta.requestGeneral.PreguntaRequestDTO;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.modelPreguntasInicial.Opcion;
import com.amani.amaniapirest.models.modelPreguntasInicial.Pregunta;
import com.amani.amaniapirest.repository.repositoryRespuesta.OpcionRepository;
import com.amani.amaniapirest.repository.repositoryRespuesta.PreguntaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * Servicio de negocio para gestionar las preguntas del cuestionario inicial de onboarding.
 */
@Service
@RequiredArgsConstructor
public class PreguntaServicio {

    private final PreguntaRepository preguntaRepository;
    private final OpcionRepository opcionRepository;

    // =========================
    // ADMIN CREA PREGUNTA
    // =========================
    public OpcionAdminResponseDTO crearPregunta(PreguntaRequestDTO dto) {

        Pregunta pregunta = new Pregunta();
        pregunta.setTexto(dto.getTexto());
        pregunta.setTipo(dto.getTipo());

        Pregunta guardada = preguntaRepository.save(pregunta);

        if (dto.getOpciones() != null) {

            for (String opcionTexto : dto.getOpciones()) {
                Opcion opcion = new Opcion();
                opcion.setTexto(opcionTexto);
                opcion.setPregunta(guardada);

                opcionRepository.save(opcion);
            }
        }
        return miAdminResponseDTO(pregunta);
    }

    // =========================
    // ADMIN ELIMINA
    // =========================
    public void eliminarPregunta(Long idPregunta) {
        preguntaRepository.deleteById(idPregunta);
    }

    // =========================
    // ADMIN ACTUALIZA
    // =========================
    public OpcionAdminResponseDTO actualizarPregunta(Long idPregunta, PreguntaRequestDTO dto) {
        Pregunta pregunta = preguntaRepository.findById(idPregunta).orElseThrow();
        pregunta.setTexto(dto.getTexto());
        pregunta.setTipo(dto.getTipo());
        Pregunta actualizada = preguntaRepository.save(pregunta);
        if (dto.getOpciones() != null) {

            for (String opcionTexto : dto.getOpciones()) {

                Opcion opcion = new Opcion();
                opcion.setTexto(opcionTexto);
                opcion.setPregunta(actualizada);

                opcionRepository.save(opcion);

            }

        }
        return miAdminResponseDTO(pregunta);
    }

    public List<OpcionAdminResponseDTO> obtenerPreguntaPaciente() {
        List<Pregunta> pacientes = preguntaRepository.findAll();
        List<OpcionAdminResponseDTO> op = new ArrayList<>();
        if (pacientes.isEmpty()) {
            return null;
        }
        pacientes.forEach(
                pregunta -> {
                    op.add(miAdminResponseDTO(pregunta));
                }
        );
        return op;
    }

    public List<PreguntaPacienteResponseDTO> obtenerPreguntaPacientes() {
        List<Pregunta> pacientes = preguntaRepository.findAll();
        List<PreguntaPacienteResponseDTO> op = new ArrayList<>();
        if (pacientes.isEmpty()) {
            return null;
        }
        pacientes.forEach(
                pregunta -> {
                    op.add(miPacienteResponseDTO(pregunta));
                }
        );
        return op;
    }


    private OpcionAdminResponseDTO miAdminResponseDTO(Pregunta pregunta) {
        OpcionAdminResponseDTO dto = new OpcionAdminResponseDTO();
        dto.setTexto(pregunta.getTexto());
        dto.setTipo(pregunta.getTipo());
        List<String> opcionesTexto = pregunta.getOpciones().stream()
                .map(Opcion::getTexto)
                .toList();

        dto.setOpciones(opcionesTexto);

        dto.setCreadoEn(pregunta.getCreadoEn());
        return dto;
    }

    private PreguntaPacienteResponseDTO miPacienteResponseDTO(Pregunta pregunta) {
        PreguntaPacienteResponseDTO dto = new PreguntaPacienteResponseDTO();
        dto.setTexto(pregunta.getTexto());
        dto.setTipo(pregunta.getTipo());
        List<String> opcionesTexto = pregunta.getOpciones().stream()
                .map(Opcion::getTexto)
                .toList();

        dto.setOpciones(opcionesTexto);

        return dto;
    }
}