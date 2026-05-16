package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import com.amani.amaniapirest.enums.RolUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
class PacientesRepositoryTest {

    @Autowired
    private PacientesRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("findByUsuario_IdUsuario devuelve paciente cuando existe")
    void findByUsuarioIdUsuario_devuelvePaciente_cuandoExiste() {
        Usuario u = new Usuario(); u.setNombre("Ana"); u.setEmail("ana@amani.com"); u.setPassword("p"); u.setRol(RolUsuario.paciente);
        usuarioRepository.save(u);
        Paciente p = new Paciente(); p.setUsuario(u);
        repository.save(p);

        Optional<Paciente> result = repository.findByUsuario_IdUsuario(u.getIdUsuario());

        assertThat(result).isPresent();
        assertThat(result.get().getUsuario().getEmail()).isEqualTo("ana@amani.com");
    }

    @Test
    @DisplayName("findByUsuario_IdUsuario devuelve empty cuando no existe")
    void findByUsuarioIdUsuario_devuelveEmpty_cuandoNoExiste() {
        Optional<Paciente> result = repository.findByUsuario_IdUsuario(999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findPacientesSinPsicologo devuelve lista vacía cuando todos tienen psicólogo")
    void findPacientesSinPsicologo_devuelveVacio_cuandoTodosTienenPsicologo() {
        List<Paciente> result = repository.findPacientesSinPsicologo();
        assertThat(result).isEmpty();
    }
}
