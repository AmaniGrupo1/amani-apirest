package com.amani.amaniapirest.services;

import com.amani.amaniapirest.models.Consentimiento;
import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.repository.ConsentimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio que implementa la lógica de negocio para Consentimiento.
 *
 * <p>Coordina las operaciones principales y gestiona las reglas de dominio.</p>
 *
 * Servicio principal que implementa la lógica de negocio de ConsentimientoService.
 *
 * <p>Responsable de gestionar las reglas de dominio y validaciones correspondientes.</p>
 */
@Service
@RequiredArgsConstructor
public class ConsentimientoService {

    private final ConsentimientoRepository consentimientoRepository;

    // versión actual de los términos (IMPORTANTE)
    private static final String VERSION_TERMINOS = "v1.0";


    /**
     * Crea y persiste un nuevo registro en el sistema.
     *
     * @return Resultado de la operación o entidad procesada.
     */
    public void guardarConsentimiento(
            Paciente paciente,
            boolean aceptaTerminos,
            boolean aceptaVideoconferencia,
            boolean aceptaComunicacion
    ) {

        // Validación obligatoria
        if (!aceptaTerminos) {
            throw new RuntimeException("Debe aceptar los términos y condiciones");
        }

        Consentimiento consentimiento = new Consentimiento();

        consentimiento.setPaciente(paciente);
        consentimiento.setFechaAceptacion(LocalDateTime.now());
        consentimiento.setVersionDocumento(VERSION_TERMINOS);
        consentimiento.setAceptaVideoconferencia(aceptaVideoconferencia);
        consentimiento.setAceptaComunicacion(aceptaComunicacion);

        consentimientoRepository.save(consentimiento);
    }
}
