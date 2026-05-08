package com.amani.amaniapirest.services.situacionService;

import com.amani.amaniapirest.dto.situacion.SituacionDTO;
import com.amani.amaniapirest.dto.situacion.SituacionRequest;
import com.amani.amaniapirest.models.Situacion;
import com.amani.amaniapirest.repository.SituacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SituacionServiceTest {

    @Mock
    private SituacionRepository repository;

    @InjectMocks
    private SituacionService service;

    @Test
    void createExitoso() {
        SituacionRequest request = new SituacionRequest("Estrés", "Laboral", "Presión laboral", true);
        Situacion saved = Situacion.builder().idSituacion(1L).nombre("Estrés").categoria("Laboral").descripcion("Presión laboral").activo(true).build();
        when(repository.save(any(Situacion.class))).thenReturn(saved);

        SituacionDTO result = service.create(request);

        assertThat(result.getIdSituacion()).isEqualTo(1L);
        assertThat(result.getNombre()).isEqualTo("Estrés");
    }

    @Test
    void updateExitoso() {
        Situacion existing = Situacion.builder().idSituacion(1L).nombre("Viejo").categoria("Cat").descripcion("Desc").activo(true).build();
        SituacionRequest request = new SituacionRequest("Nuevo", "Cat2", "Desc2", true);
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Situacion.class))).thenAnswer(inv -> inv.getArgument(0));

        SituacionDTO result = service.update(1L, request);

        assertThat(result.getNombre()).isEqualTo("Nuevo");
    }

    @Test
    void updateNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, new SituacionRequest("X", "Y", "Z", true)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Situación no encontrada");
    }

    @Test
    void deleteExitoso() {
        Situacion existing = Situacion.builder().idSituacion(1L).nombre("X").categoria("Y").descripcion("Z").activo(true).build();
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Situacion.class))).thenAnswer(inv -> inv.getArgument(0));

        service.delete(1L);

        assertThat(existing.getActivo()).isFalse();
        verify(repository).save(existing);
    }

    @Test
    void deleteNoExiste() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Situación no encontrada");
    }
}
