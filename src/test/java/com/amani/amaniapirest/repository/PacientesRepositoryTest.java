package com.amani.amaniapirest.repository;

import com.amani.amaniapirest.models.Paciente;
import com.amani.amaniapirest.models.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PacientesRepositoryTest {

    @Autowired
    private PacientesRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("findByUsuario_IdUsuario devuelve paciente cuando existe")
    void findByUsuarioIdUsuario_devuelvePaciente_cuandoExiste() {
        Usuario u = new Usuario(); u.setNombre("Ana"); u.setEmail("ana@amani.com"); u.setPassword("p");
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
